(ns culture.facts-test
  (:require [clojure.edn :as edn]
            [kotoba.lang.text :as str]
            [clojure.test :refer [deftest is]]
            [culture.facts :as facts]))

(deftest hti-has-culture-basis
  (let [sb (facts/spec-basis "HTI")]
    (is (= 9 (count sb)))
    (is (= (count sb) (count (set (map :culture/id sb)))))
    (is (every? #(str/starts-with? (:culture/url %) "https://") sb))
    (is (every? #(= "HTI" (:culture/country %)) sb))
    (is (every? #(nil? (:culture/municipality %)) sb))
    (is (every? #(seq (:culture/summary %)) sb))
    (is (every? #(string? (:culture/retrieved-at %)) sb))))

(deftest unknown-jurisdiction-has-no-basis
  (is (nil? (facts/spec-basis "DOM")))
  (is (nil? (facts/spec-basis "zzz"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["HTI" "DOM"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["DOM"] (:missing-jurisdictions c)))))

(deftest by-kind-filters
  (is (= 4 (count (facts/by-kind "HTI" :dish))))
  (is (= ["hti.heritage.citadelle-laferriere"]
         (mapv :culture/id (facts/by-kind "HTI" :heritage))))
  (is (empty? (facts/by-kind "HTI" :other)))
  (is (empty? (facts/by-kind "DOM" :dish))))

(deftest tx-file-matches-catalog
  (let [tx (edn/read-string (slurp "data/culture-tx.edn"))
        flat (mapcat val (sort-by key facts/catalog))]
    (is (= (vec flat) (vec tx)))))
