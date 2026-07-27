(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-sector market-entry filing --
  every jurisdiction assigns its own format. This namespace does NOT
  invent one; it builds a jurisdiction-scoped sequence number and
  validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `marche-public-required?` / `marche-public-claim-mismatch?` are the
  SAME discipline applied to a genuinely Republic of Haiti-specific
  mechanism: Arrêté du 23 février 2026 (Le Moniteur, 181ème Année,
  Spécial No 14, Mardi 24 Mars 2026) Articles 3/4/5/6's own CATEGORY-
  GATED seuil-de-passation figures -- the monetary threshold above which
  a formal Marché public (subject to the Loi du 10 juin 2009's full
  procedure) applies AT ALL, as opposed to 'commande hors marché' (achat
  sur simple mémoire/facture, or consultation de fournisseurs/
  sollicitation de prix, per Art. 2), depends on WHICH of four tiers of
  Haiti's own territorial-administration hierarchy the contracting
  authority belongs to (État/Entreprises publiques/Collectivités
  Départementales; Communes Chefs-lieux de Départements + six Port-au-
  Prince-metro communes; Chefs-lieux d'Arrondissements; other Communes/
  Sections communales).

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors: Cuba's Ley 118 Art. 21 mechanism is a
  SECTOR/MODALITY AUTHORITY-JURISDICTION ROUTING classification, CAF's
  Marché réservé mechanism is a MULTI-CRITERION WORKFORCE-COMPOSITION
  ELIGIBILITY test, and Dominica's Second-Schedule mechanism is a SINGLE
  NATIONAL value-escalation ladder applying government-wide. Haiti's
  Arrêté du 23 février 2026 mechanism is none of these: it is a
  CATEGORY-GATED SINGLE-THRESHOLD APPLICABILITY GATE -- the numeric
  threshold itself (and hence whether the Marché-public apparatus
  applies at all) varies by WHICH TIER of the contracting authority's
  own territorial-administration category, not by sector, workforce
  composition, or a single government-wide ladder.

  This iteration deliberately modeled only the seuil-de-passation layer
  (Art. 3/4/5/6 -- does a formal Marché public apply at all), not the
  richer seuils-d'intervention-CNMP sub-thresholds (Art. 3.1/4-1/5.1,
  which further split by Marché de travaux/fournitures/services within
  each category) -- the same honest scope-narrowing discipline CUB's
  two-of-three-tier Ley 118 Art. 21 model and CAF's unmodeled
  ministerial-arrêté value threshold already established for this
  family: model the cleanest, most legible layer completely and
  correctly rather than the whole matrix partially.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real government system. It builds the RECORD an operator
  would keep, not the act of submitting a portal registration itself
  (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(def ^:private money-scale
  "Sub-minor-unit scale used when comparing two money amounts: 1/10000 of
  a unit. Coarser than double representation error by many orders of
  magnitude, finer than any real currency's minor unit (2 decimals for
  most, 3 for KWD/BHD/OMR, 0 for JPY/KRW)."
  10000)

(defn- money=
  "Exact-at-money-precision equality for two amounts.

  `==` on raw doubles is NOT the right comparison for money. With
  whole-unit fees the two agree, but as soon as an amount carries
  cents the sum `base + rate x months` is routinely not the double
  nearest the true total, and a CORRECT claim compares false: measured
  on this exact shape, 40,989 of 327,060 cent-denominated combinations
  (12.5%) were rejected while being right, against 0 of 327,060 in
  whole units.

  Rounding both sides to `money-scale` before comparing removes the
  representation error while preserving every distinction money can
  actually carry."
  [x y]
  (and (number? x) (number? y)
       (= (Math/round (* money-scale (double x)))
          (Math/round (* money-scale (double y))))))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  ;; nil when any field is not a number: an un-recomputable engagement is
  ;; un-verifiable, which is neither `correct` nor a ClassCastException
  ;; thrown out of the caller.
  (when (and (number? base-fee) (number? monthly-rate) (number? monitoring-months))
    (+ (double base-fee)
       (* (double monthly-rate) (double monitoring-months)))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (money= claimed-fee (compute-engagement-fee engagement)))

(def marche-public-seuil-htg
  "Seuil de passation des Marchés publics (Arrêté du 23 février 2026,
  Articles 3/4/5/6), in HTG, by contracting-authority category -- at or
  above which a formal Marché public applies (per Art. 2, below it a
  purchase is 'commande hors marché': achat sur simple mémoire/facture,
  or consultation de fournisseurs/sollicitation de prix, still subject to
  Comptabilité-publique rules but dispensed from Marché-public
  procedure). A category not modeled here falls through to the LOWEST
  (strictest) threshold -- `:autres-communes-sections-communales` -- never
  to a more permissive one, the same fail-strict discipline
  `investment-approval-authority`-style siblings apply to their own
  residual/default bucket."
  {:etat-collectivites-departementales-entreprises-publiques 15000000.0
   :communes-cheflieux-departement-et-metropolitaines-pap 5500000.0
   :communes-cheflieux-arrondissement 4500000.0
   :autres-communes-sections-communales 4000000.0})

(defn marche-public-required?
  "The ground-truth answer, independently recomputed from `engagement`'s
  own declared `:contracting-authority-category` and
  `:contract-value-htg`, to 'does a formal Marché public apply' per
  Arrêté du 23 février 2026 Art. 3/4/5/6. A missing/unrecognized category
  falls through to the strictest (lowest) threshold rather than
  defaulting permissively."
  [{:keys [contracting-authority-category contract-value-htg]}]
  (let [seuil (get marche-public-seuil-htg contracting-authority-category
                    (:autres-communes-sections-communales marche-public-seuil-htg))]
    (>= (double (or contract-value-htg 0)) seuil)))

(defn marche-public-claim-mismatch?
  "Does `engagement` declare a `:claimed-marche-public?` that does NOT
  match the independently recomputed `marche-public-required?`? An
  engagement with no claim at all is not flagged by this check (entity/
  engagement-scope-gated, the same discipline CUB's
  `:claimed-approval-authority`-gated check and CAF's
  `:reserved-market?`-gated eligibility check use)."
  [{:keys [claimed-marche-public?] :as engagement}]
  (boolean (and (some? claimed-marche-public?)
                (not= claimed-marche-public? (marche-public-required? engagement)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real government system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting the Marché-
  public filing / Autorisation de Fonctionnement registration (always
  human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
