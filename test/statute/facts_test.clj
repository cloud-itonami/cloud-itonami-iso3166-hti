(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest hti-has-spec-basis
  (let [sb (facts/spec-basis "HTI")]
    (is (= 2 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["HTI" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= #{"hti.loi-10juin2009-marches-publics"}
         (set (mapv :statute/id (facts/by-topic "HTI" :public-procurement)))))
  (is (= #{"hti.decret-2005-nif-matricule-fiscale"}
         (set (mapv :statute/id (facts/by-topic "HTI" :tax)))))
  (is (empty? (facts/by-topic "ATL" :tax))))
