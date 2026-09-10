# Business Model — Republic of Haiti

## Offer

- Commission Nationale des Marchés Publics (CNMP) public-procurement
  a priori control -- Loi du 10 juin 2009 (Loi No. CL 06 2009-009)
  "fixant les règles générales relatives aux Marchés publics et aux
  Conventions de Concession d'ouvrage de Service public" and its
  currently-governing implementing Arrêté du 23 février 2026 (Le
  Moniteur, 181ème Année, Spécial No 14, Mardi 24 Mars 2026). No
  self-service e-procurement portal; CNMP publishes Marchés passés,
  Plans annuels, Avis d'attribution and a live public Liste Noire
  (blacklist) directly on its own site. A category-specific seuil de
  passation (Art. 3/4/5/6) gates whether a formal Marché public applies
  at all -- a case-by-case APPLICABILITY GATE, not a competitive-tender
  self-service registration system (see `src/marketentry/facts.kotoba`)
- Ministère du Commerce et de l'Industrie (MCI) Autorisation de
  Fonctionnement business registration -- required for a Société
  Anonyme to acquire legal existence (own official five-stage
  procedure: recherche d'antériorité, dépôt des pièces, examen DAJ,
  publicités légales in Le Moniteur, déblocage de fonds)
- Direction Générale des Impôts (DGI) Numéro d'Immatriculation Fiscale
  (NIF) tax registration
- Marché-public seuil-de-passation gate (flagship check) -- bars a
  filing that claims the wrong Marché-public applicability for its own
  declared contracting-authority category and contract value under
  Arrêté du 23 février 2026 Art. 3/4/5/6
- CNMP Liste Noire (blacklist) verification gate -- bars a filing that
  requires a blacklist check but has none on file
- paper-only filing for most business-registration steps (MCI's own
  text: "uniquement par courrier traditionnel"); a live e-registration
  portal (`guichet.mci.ht`) exists for individual-enterprise
  registration, gated on a Numéro d'Identification National Unique
  (NINU)

## Trust Controls

- Any actual CNMP filing or MCI Autorisation de Fonctionnement
  registration requires Market-Entry Compliance Governor clearance and
  always escalates to human sign-off.
- A false or fabricated regulatory-requirement claim is a HARD hold.
- `:filing/submit` never automated
