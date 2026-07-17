(ns culture.facts
  "Country-level regional-culture catalog for Haiti (HTI) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  the `marketentry.facts` / `statute.facts` catalogs of the iso3166
  siblings (ADR-2607141700); city-level counterparts live in the
  cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"HTI"
   [{:culture/id "hti.dish.griot"
     :culture/name "Griot"
     :culture/country "HTI"
     :culture/kind :dish
     :culture/summary "Dish in Haitian cuisine of pork shoulder marinated in citrus, braised and then fried."
     :culture/url "https://en.wikipedia.org/wiki/Griot_(food)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "hti.dish.soup-joumou"
     :culture/name "Soup joumou"
     :culture/country "HTI"
     :culture/kind :dish
     :culture/summary "Haitian pumpkin soup traditionally eaten on New Year's Day (January 1) to commemorate Haitian independence; inscribed on UNESCO's intangible cultural heritage list in December 2021, Haiti's first such recognition."
     :culture/url "https://en.wikipedia.org/wiki/Soup_joumou"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "hti.dish.diri-djondjon"
     :culture/name "Diri djondjon"
     :culture/country "HTI"
     :culture/kind :dish
     :culture/summary "Native Haitian dish of rice cooked with edible black djondjon mushrooms that give it a distinctive dark color, more common in the north of the country."
     :culture/url "https://en.wikipedia.org/wiki/Diri_djondjon"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "hti.dish.pikliz"
     :culture/name "Pikliz"
     :culture/country "HTI"
     :culture/kind :dish
     :culture/summary "Pickled condiment in Haitian cuisine made with scotch bonnet peppers, cabbage, carrots and other vegetables in vinegar or citrus juice."
     :culture/url "https://en.wikipedia.org/wiki/Pikliz"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "hti.beverage.rhum-barbancourt"
     :culture/name "Rhum Barbancourt"
     :culture/country "HTI"
     :culture/kind :beverage
     :culture/summary "Rum produced from pure sugar cane juice and bottled in Haiti by a family-owned distillery established in 1862."
     :culture/url "https://en.wikipedia.org/wiki/Rhum_Barbancourt"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "hti.beverage.clairin"
     :culture/name "Clairin"
     :culture/country "HTI"
     :culture/kind :beverage
     :culture/summary "Distilled alcoholic spirit made from sugarcane produced in Haiti, using the same distillation process as rhum agricole though less refined."
     :culture/url "https://en.wikipedia.org/wiki/Clairin"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "hti.festival.haitian-carnival"
     :culture/name "Haitian Carnival"
     :culture/name-local "Kanaval"
     :culture/country "HTI"
     :culture/kind :festival
     :culture/summary "Celebration held over several weeks each year leading up to Mardi Gras, with parades, music and costumes in Port-au-Prince and other Haitian cities."
     :culture/url "https://en.wikipedia.org/wiki/Haitian_Carnival"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "hti.festival.rara"
     :culture/name "Rara"
     :culture/country "HTI"
     :culture/kind :festival
     :culture/summary "Lent-season festival and form of festival music originating in Haiti."
     :culture/url "https://en.wikipedia.org/wiki/Rara"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "hti.heritage.citadelle-laferriere"
     :culture/name "Citadelle Laferrière"
     :culture/country "HTI"
     :culture/kind :heritage
     :culture/summary "Massive early 19th-century fortress built under Henri Christophe to defend Haiti's independence; part of the National History Park, a UNESCO World Heritage Site designated in 1982."
     :culture/url "https://en.wikipedia.org/wiki/Citadelle_Laferri%C3%A8re"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-hti culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "HTI"))
                 " HTI entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
