(ns statute.facts
  "General-law compliance catalog for the Republic of Haiti (HTI) --
  extends this repo's existing `marketentry.facts` (public-sector
  market-entry/procurement only, narrow scope) with a second, orthogonal
  catalog of national statutes a foreign investor operating in this
  jurisdiction must generally track for compliance. Mirrors
  cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/-arm/-atg/-ben/-btn/-caf/
  -cub's `statute.facts` (ADR-2607141700, cloud-itonami-compliance-fact-
  federation).

  Every entry cites an OFFICIAL government-hosted source -- never
  fabricated. Both entries below were fetched directly (curl/OCR/visual-
  crop-verified, 2026-07-22/23) from CNMP's own hosted PDF
  (`cnmp.gouv.ht`) and DGI's own live service page (`dgi.gouv.ht`)
  respectively.

  - **Public procurement**: Loi du 10 juin 2009 (Loi No. CL 06 2009-009)
    -- the SAME law `marketentry.facts` uses as its market-entry spec-
    basis; it is ALSO catalogued here as a general national-law
    reference, since a foreign investor tracks it both as a market-entry
    gate (per `:national-spec`) and as an ongoing compliance statute
    (any Marché public a company enters into after market entry remains
    governed by this same law and its implementing arrêtés).
  - **Tax registration / NIF**: this iteration specifically investigated,
    rather than assumed, Haiti's business-tax-identifier mechanism.
    DGI's own official 'La Matricule Fiscale' service page
    (`dgi.gouv.ht/dgi_sev/la-matricule-fiscale/`, fetched directly,
    quoted verbatim) confirms the Numéro d'Immatriculation Fiscale (NIF)
    obligation for both natural and legal persons. NOTE: DGI's own page
    is internally inconsistent about which décret governs which specific
    provision -- it names THREE distinct décret dates ('décret du 1er
    juin 2005 relatif à la carte d'identification nationale' for the
    CIF's general framework; 'déc. du 20/10/05' / 'Art. 2 déc. du 20 Oct.
    05' for the NIF obligation itself; 'décret du 29 septembre 2005
    modifiant celui du 28 septembre 1987' for the fee schedule) without
    reconciling them into one instrument -- this catalog reports this
    exactly as DGI's own page states it, flagging the inconsistency
    rather than silently resolving it, and did NOT independently fetch
    any of the three décrets' own Le Moniteur primary text (only DGI's
    own citations of them).
  - **Labor law (Code du Travail)**: this iteration extensively searched
    for Haiti's own Code du Travail primary text and could NOT
    independently confirm it this session -- `mast.gouv.ht` (DNS
    failure), `justice.gouv.ht` (DNS failure), `mjsp.gouv.ht` (HTTP 200
    with an empty body on every attempt), `primature.gouv.ht`'s own
    'textes-de-loi' link (live HTTP 404), `droit-afrique.com` (HTTP 403
    Forbidden on both `curl` with full browser headers AND WebFetch
    independently), and the ILO's NATLEX database (HTTP 403 Forbidden)
    were all attempted and all failed to yield the primary text or even
    a confirmable décret/loi number. OFATMA's own 'Cadre légal' page
    (`ofatma.gouv.ht`, fetched directly) confirms the labor ministry's
    name (Ministère des Affaires Sociales et du Travail, MAST) and cites
    several labor-ADJACENT social-insurance instruments (loi organique
    du MAST du 28 août 1967; Décret du 18 février 1975; Loi du 4 novembre
    1983; Décret du 17 mai 2005 Art. 153) but NONE of these is the Code
    du Travail itself. Given this genuine, well-documented difficulty
    (mirroring Eritrea's/Equatorial Guinea's precedent in this fleet),
    this catalog deliberately does NOT include a labor-code entry --
    a smaller, honest catalog beats an invented citation.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"HTI"
   [{:statute/id "hti.loi-10juin2009-marches-publics"
     :statute/title "Loi du 10 juin 2009 \"fixant les règles générales relatives aux Marchés publics et aux Conventions de Concession d'ouvrage de Service public\""
     :statute/jurisdiction "HTI"
     :statute/kind :law
     :statute/law-number "Loi No. CL 06 2009-009 (own text, read directly via OCR of the reproduction in Le Moniteur No. 78, Mardi 28 Juillet 2009, itself a reproduction pour erreurs matérielles of the original text in Le Moniteur No. 60, Vendredi 12 Juin 2009)"
     :statute/url "https://www.cnmp.gouv.ht/uploads/documents/1760912867_1-loimarchespublicsdu10juin2009.pdf"
     :statute/url-provenance :official-cnmp-gouv-ht
     :statute/enacted-date "2009-06-10"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:public-procurement}}
    {:statute/id "hti.decret-2005-nif-matricule-fiscale"
     :statute/title "Décret(s) relatif(s) à la Carte d'Identité Fiscale / Numéro d'Immatriculation Fiscale (NIF)"
     :statute/jurisdiction "HTI"
     :statute/kind :decree
     :statute/law-number "DGI's own service page names THREE distinct décret dates without reconciling them into one instrument: décret du 1er juin 2005 relatif à la carte d'identification nationale (CIF general framework, Art. 3/5/6); déc. du 20/10/05 / Art. 2 déc. du 20 Oct. 05 (NIF obligation for natural and legal persons); décret du 29 septembre 2005 modifiant celui du 28 septembre 1987 relatif à la Carte d'Identité Fiscale (fee schedule, Art. 11) -- this catalog reports this exactly as DGI states it, an honest unresolved inconsistency (see namespace docstring), and did not independently fetch any of the three décrets' own Le Moniteur primary text"
     :statute/url "https://dgi.gouv.ht/dgi_sev/la-matricule-fiscale/"
     :statute/url-provenance :official-dgi-gouv-ht
     :statute/enacted-date "2005-10-20"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:tax :incorporation}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-hti statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "HTI")) " HTI statute(s) seeded with an "
                 "official citation. Extend `statute.facts/catalog`, never "
                 "fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
