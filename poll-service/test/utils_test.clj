(ns utils-test
  (:require [utils :refer [parse-date reset-minutes-str]]
            [clojure.test :refer :all]))

(deftest test-parse-date
  (testing "date parsed as string."
    (is (not (nil? (parse-date "2018-01-01T12:00:00Z"))))
    (is (thrown? Exception (parse-date "Loren Ipsuns")))))

(deftest test-reset-minutes-str
  (testing "string with zero minutes and seconds."
    (are [expected s] (= expected (reset-minutes-str s))
      "2018-01-01T00:00:00Z" "2018-01-01T00:59:59Z"
      "2018-01-01T12:00:00Z" "2018-01-01T12:59:59Z"
      "2018-01-01T23:00:00Z" "2018-01-01T23:59:59Z")))
