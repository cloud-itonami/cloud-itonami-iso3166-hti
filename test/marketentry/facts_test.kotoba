(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest hti-has-spec-basis
  (let [sb (facts/spec-basis "HTI")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "HTI")))
    (is (some? (facts/marche-public-spec-basis "HTI")))))

(deftest hti-rep-spec-basis-is-honestly-absent
  (testing "no verifiable statutory basis for the CNMP Liste Noire mechanism was located -- deliberately not claimed"
    (is (nil? (facts/rep-spec-basis "HTI")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "HTI")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "HTI" all)))
    (is (not (facts/required-evidence-satisfied? "HTI" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["HTI" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest marche-public-spec-basis-criteria
  (let [mp (facts/marche-public-spec-basis "HTI")]
    (is (= 15000000.0
           (get-in mp [:marche-public-criteria :etat-collectivites-departementales-entreprises-publiques])))
    (is (= 5500000.0
           (get-in mp [:marche-public-criteria :communes-cheflieux-departement-et-metropolitaines-pap])))
    (is (= 4500000.0
           (get-in mp [:marche-public-criteria :communes-cheflieux-arrondissement])))
    (is (= 4000000.0
           (get-in mp [:marche-public-criteria :autres-communes-sections-communales])))))
