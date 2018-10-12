(ns domain-test
  (:require [domain :refer [breakdown-by]]
            [clojure.test :refer :all]))

(deftest test-breakdown-by-candidate
  (testing "breakdown of votes by candidate."
    (is (empty? (breakdown-by :candidate [])))
    (is (map? (breakdown-by :candidate [])))
    (is (= (breakdown-by :candidate [{:candidate "sister"}
                                     {:candidate "brother"}
                                     {:candidate "sister"}
                                     {:candidate "brother"}
                                     {:candidate "sister"}])
           {"sister" 3
            "brother" 2}))))
