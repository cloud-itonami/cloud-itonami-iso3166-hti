(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "HTI" 0)
        s (registry/register-submit "eng-1" "HTI" 0)]
    (is (= "HTI-DFT-000000" (get d "draft_number")))
    (is (= "HTI-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "HTI" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest marche-public-etat-category
  (testing "État/Collectivités Départementales/Entreprises publiques -- seuil de 15,000,000 HTG (Art. 3)"
    (is (false? (registry/marche-public-required?
                 {:contracting-authority-category :etat-collectivites-departementales-entreprises-publiques
                  :contract-value-htg 14999999.0})))
    (is (true? (registry/marche-public-required?
                {:contracting-authority-category :etat-collectivites-departementales-entreprises-publiques
                 :contract-value-htg 15000000.0})))))

(deftest marche-public-communes-metropolitaines-category
  (testing "Communes chefs-lieux de départements + 6 communes métropolitaines -- seuil de 5,500,000 HTG (Art. 4)"
    (is (false? (registry/marche-public-required?
                 {:contracting-authority-category :communes-cheflieux-departement-et-metropolitaines-pap
                  :contract-value-htg 5499999.0})))
    (is (true? (registry/marche-public-required?
                {:contracting-authority-category :communes-cheflieux-departement-et-metropolitaines-pap
                 :contract-value-htg 5500000.0})))))

(deftest marche-public-arrondissement-category
  (testing "Chefs-lieux d'arrondissements -- seuil de 4,500,000 HTG (Art. 5)"
    (is (false? (registry/marche-public-required?
                 {:contracting-authority-category :communes-cheflieux-arrondissement
                  :contract-value-htg 4499999.0})))
    (is (true? (registry/marche-public-required?
                {:contracting-authority-category :communes-cheflieux-arrondissement
                 :contract-value-htg 4500000.0})))))

(deftest marche-public-autres-communes-category-and-residual-default
  (testing "Autres communes/sections communales -- seuil de 4,000,000 HTG (Art. 6)"
    (is (false? (registry/marche-public-required?
                 {:contracting-authority-category :autres-communes-sections-communales
                  :contract-value-htg 3999999.0})))
    (is (true? (registry/marche-public-required?
                {:contracting-authority-category :autres-communes-sections-communales
                 :contract-value-htg 4000000.0}))))
  (testing "an unrecognized/missing category falls through to the strictest (lowest) threshold"
    (is (true? (registry/marche-public-required?
                {:contracting-authority-category :unknown-category
                 :contract-value-htg 4000000.0})))
    (is (false? (registry/marche-public-required? {:contract-value-htg 0})))))

(deftest marche-public-claim-mismatch-is-entity-scope-gated
  (testing "an engagement with no claim at all is never flagged"
    (is (false? (registry/marche-public-claim-mismatch?
                 {:contracting-authority-category :autres-communes-sections-communales
                  :contract-value-htg 4200000.0}))))
  (testing "a claim that does NOT match the independently recomputed classification -> mismatch"
    (is (true? (registry/marche-public-claim-mismatch?
                {:contracting-authority-category :autres-communes-sections-communales
                 :contract-value-htg 4200000.0
                 :claimed-marche-public? false}))))
  (testing "a claim that DOES match -> not flagged"
    (is (false? (registry/marche-public-claim-mismatch?
                 {:contracting-authority-category :autres-communes-sections-communales
                  :contract-value-htg 4200000.0
                  :claimed-marche-public? true})))
    (is (false? (registry/marche-public-claim-mismatch?
                 {:contracting-authority-category :etat-collectivites-departementales-entreprises-publiques
                  :contract-value-htg 1000000.0
                  :claimed-marche-public? false})))))
