# cloud-itonami-iso3166-hti

Open ISO 3166 Blueprint for **HTI**: Republic of Haiti.

- Commission Nationale des Marchés Publics (CNMP) -- public-procurement
  a priori control, per the Loi du 10 juin 2009 (Loi No. CL 06 2009-009)
  and its currently-governing implementing Arrêté du 23 février 2026
  (Le Moniteur, 181ème Année, Spécial No 14), a CATEGORY-GATED
  seuil-de-passation regime (Art. 3/4/5/6), not a competitive-tender
  self-service e-procurement portal
- Ministère du Commerce et de l'Industrie (MCI) business registration
  (Autorisation de Fonctionnement des Sociétés Anonymes) + Direction
  Générale des Impôts (DGI) Numéro d'Immatriculation Fiscale (NIF) tax
  registration

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as `cloud-itonami-iso3166-btn`/`-bwa`/`-caf`/`-cub`, adapted to Haiti's
genuinely hard-to-verify market-entry surface (verified 2026-07-22/23,
see the namespace docstrings for the full research trail, including
facts this iteration could NOT verify -- most notably Haiti's own Code
du Travail, which this iteration extensively searched for across six
official/secondary sources and honestly could not confirm this session):

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites Loi du 10 juin
  2009 (Loi No. CL 06 2009-009) "fixant les règles générales relatives
  aux Marchés publics et aux Conventions de Concession d'ouvrage de
  Service public" (OCR-read from CNMP's own hosted scan of Le Moniteur
  No. 78) and its currently-governing implementing Arrêté du 23 février
  2026 (Le Moniteur, 181ème Année, Spécial No 14, Mardi 24 Mars 2026,
  read directly -- a clean, legible scan, no OCR needed): CNMP admits/
  controls Marchés publics a priori above category-specific thresholds
  (Art. 3/4/5/6); Ministère du Commerce et de l'Industrie (MCI)
  Autorisation de Fonctionnement business registration (own official
  five-stage procedure page, read directly); Direction Générale des
  Impôts (DGI) Numéro d'Immatriculation Fiscale (NIF) tax registration
  (own official service page, read directly, with an honestly-flagged
  internal date inconsistency across three cited décrets).
  `governor.cljc`'s flagship check independently recomputes whether an
  engagement's own declared contracting-authority category and contract
  value actually cross the seuil de passation Arrêté du 23 février 2026
  Art. 3/4/5/6 assigns to that category -- a CATEGORY-GATED SINGLE-
  THRESHOLD APPLICABILITY GATE, a shape genuinely different from every
  other iso3166 sibling's (Cuba's sector/modality authority-routing,
  CAF's workforce-composition eligibility test, Dominica's single
  national value-escalation ladder): the threshold ITSELF, and hence
  whether the Marché-public apparatus applies at all, depends on WHICH
  TIER of Haiti's own territorial-administration hierarchy (Décret du
  1er février 2006 x3) the contracting authority belongs to. A second,
  simpler check independently verifies CNMP's own live "Liste Noire"
  (blacklist) has actually been checked when required -- this iteration
  confirmed the Liste Noire is real and currently active (two live
  exclusions read directly from `cnmp.gouv.ht`) but did not locate the
  specific statutory article establishing it, an honestly-disclosed gap
  (see `marketentry.facts` docstring).
- `src/statute/facts.kotoba` -- general-law catalog: the same Loi du 10
  juin 2009 (also catalogued here as an ongoing compliance statute, not
  just a market-entry gate), and DGI's own NIF/Carte d'Identité Fiscale
  décret citations (with the internal date inconsistency flagged rather
  than silently resolved). Haiti's Code du Travail (labor law) is
  deliberately NOT catalogued -- six independent verification attempts
  (`mast.gouv.ht`, `justice.gouv.ht`, `mjsp.gouv.ht`,
  `primature.gouv.ht`'s own "textes-de-loi" link, `droit-afrique.com`,
  and the ILO's NATLEX database) all failed to yield a confirmable
  primary text or even a décret/loi number this session -- an honest gap,
  not an invented citation.

Every citation is curl/OCR/visual-crop-verified against an official
source (`cnmp.gouv.ht`, `dgi.gouv.ht`, `mci.gouv.ht`, `ofatma.gouv.ht`);
CNMP's own site's internally-consistent Arrêté du 23 février 2026 text
was independently re-verified by rendering and directly viewing the
scanned page images (not just OCR text) for every numeric threshold this
catalog cites.

## Culture catalog

This repo carries a **country-level regional-culture catalog**
(ADR-2607171400 addendum 2, `cloud-itonami-municipality-culture-catalog`
Wave 1, in `com-junkawasaki/root`) — national dishes, protected products,
beverages, crafts, festivals and heritage sites for Haiti:

- `src/culture/facts.kotoba` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring the fleet's `statute.facts` convention).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
