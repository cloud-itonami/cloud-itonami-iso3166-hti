(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Republic of Haiti market-entry law, whether a claimed
  engagement fee actually equals base + months x rate, whether the
  engagement's own declared contracting-authority category and contract
  value actually cross the seuil de passation des Marchés publics the
  engagement CLAIMS under Arrêté du 23 février 2026 Art. 3/4/5/6, whether
  the CNMP Liste Noire (blacklist) has actually been checked for a filing
  that requires it, or when a draft stops being a draft and becomes a
  real-world CNMP filing / MCI Autorisation de Fonctionnement
  registration, so this MUST be a separate system able to *reject* a
  proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls: 'any
  actual CNMP filing or MCI Autorisation de Fonctionnement registration
  requires Market-Entry Compliance Governor clearance and always
  escalates to human sign-off'; 'a false or fabricated regulatory-
  requirement claim is a HARD hold') names exactly the checks below.

  Six checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Marché-public mismatch      -- for `:filing/submit`, when the
                                       engagement declares a
                                       `:claimed-marche-public?`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own declared
                                       contracting-authority category and
                                       contract value actually cross the
                                       seuil de passation per Arrêté du
                                       23 février 2026 Art. 3/4/5/6, and
                                       HARD-hold if not. FLAGSHIP
                                       genuinely new check for the
                                       iso3166 family (grep-verified
                                       absent as a governor check
                                       function name fleet-wide at build
                                       time) -- a CATEGORY-GATED SINGLE-
                                       THRESHOLD APPLICABILITY GATE, a
                                       check SHAPE genuinely different
                                       from every prior sibling's (sector/
                                       modality authority-routing /
                                       workforce-composition eligibility /
                                       single national value-escalation
                                       ladder / boolean registry
                                       membership) -- the first in this
                                       family to check whether a formal
                                       Marché public applies AT ALL, with
                                       the threshold itself gated by WHICH
                                       TIER of the contracting authority's
                                       own territorial-administration
                                       category.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. Blacklist unverified        -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-blacklist-check? true`,
                                       INDEPENDENTLY check
                                       `:blacklist-clear?`. CONDITIONAL
                                       on the engagement's own ground
                                       truth. Grounded in CNMP's own live
                                       Liste Noire (blacklist) registry
                                       of excluded economic operators
                                       (see `marketentry.facts`).
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real CNMP filing package and submitting a real filing /
  MCI Autorisation de Fonctionnement registration request are the two
  real-world actuation events this actor performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(MCI Autorisation de Fonctionnement登録/DGI NIF税務記録/CNMP Marché-public分類/Liste Noire確認/代理人確認等)が充足していない状態での提案"}]))))

(defn- marche-public-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own declared contracting-authority category and contract
  value actually cross the seuil de passation des Marchés publics the
  engagement CLAIMS -- the flagship check this vertical adds. HARD-hold
  when the engagement declares a `:claimed-marche-public?` that does not
  match the independently recomputed classification."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/marche-public-claim-mismatch? e)
        [{:rule :marche-public-mismatch
          :detail (str subject " はMarché public適用有無(" (:claimed-marche-public? e) ")を申告しているが、"
                      "独立再計算(Arrêté du 23 février 2026 Art.3/4/5/6のseuil de passation、"
                      "contracting-authority-category=" (:contracting-authority-category e) ")による正当な適用有無("
                      (registry/marche-public-required? e) ")と一致しない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- blacklist-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-blacklist-check? true`, INDEPENDENTLY check
  `:blacklist-clear?` -- CONDITIONAL on the engagement's own ground
  truth. Grounded in CNMP's own live Liste Noire (blacklist) of excluded
  economic operators."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-blacklist-check? e))
                 (not (true? (:blacklist-clear? e))))
        [{:rule :blacklist-unverified
          :detail (str subject " はCNMP Liste Noire(ブラックリスト)確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (marche-public-mismatch-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (blacklist-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
