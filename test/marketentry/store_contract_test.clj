(ns marketentry.store-contract-test
  "MemStore ≡ DatomicStore parity for the Store protocol."
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.store :as store]
            [marketentry.registry :as registry]))

(defn- exercise [s]
  (store/commit-record! s {:effect :engagement/upsert
                           :value {:id "eng-x" :operator "X SA" :jurisdiction "HTI"
                                   :base-fee 100 :monthly-rate 10 :monitoring-months 1
                                   :claimed-fee 110.0
                                   :contracting-authority-category :etat-collectivites-departementales-entreprises-publiques
                                   :contract-value-htg 20000000.0
                                   :claimed-marche-public? true
                                   :requires-blacklist-check? true :blacklist-clear? true
                                   :drafted? false :submitted? false :status :intake}})
  (store/commit-record! s {:effect :assessment/set
                           :path ["eng-x"]
                           :payload {:jurisdiction "HTI" :checklist ["a"] :spec-basis "x"}})
  (store/commit-record! s {:effect :engagement/mark-drafted :path ["eng-x"]})
  (store/commit-record! s {:effect :engagement/mark-submitted :path ["eng-x"]})
  (store/append-ledger! s {:t :committed :op :test})
  {:engagement (store/engagement s "eng-x")
   :assessment (store/assessment-of s "eng-x")
   :drafts (store/draft-history s)
   :submits (store/submit-history s)
   :ledger (store/ledger s)
   :drafted? (store/engagement-already-drafted? s "eng-x")
   :submitted? (store/engagement-already-submitted? s "eng-x")})

(deftest mem-and-datomic-parity
  (let [mem (store/seed-db)
        dat (store/datomic-seed-db)
        ;; use empty stores for parity of exercised mutations
        mem* (store/->MemStore (atom {:engagements {} :assessments {} :ledger []
                                      :draft-sequences {} :draft-records []
                                      :submit-sequences {} :submit-records []}))
        dat* (store/datomic-store {})
        m (exercise mem*)
        d (exercise dat*)]
    (is (= (:operator (:engagement m)) (:operator (:engagement d))))
    (is (= (:contracting-authority-category (:engagement m)) (:contracting-authority-category (:engagement d))))
    (is (= (:claimed-marche-public? (:engagement m)) (:claimed-marche-public? (:engagement d))))
    (is (true? (:drafted? m)) (true? (:drafted? d)))
    (is (true? (:submitted? m)) (true? (:submitted? d)))
    (is (= 1 (count (:drafts m))) (= 1 (count (:drafts d))))
    (is (= 1 (count (:submits m))) (= 1 (count (:submits d))))
    (is (= 1 (count (:ledger m))) (= 1 (count (:ledger d))))
    (is (= (:assessment m) (:assessment d)))))
