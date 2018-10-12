(ns storage.vote-test
  (:require [com.stuartsierra.component :as component]
            [system :refer [unit-test-system]]
            [storage.vote :refer [new-store get-votes add-vote remove-all]]
            [infra.config :refer [load-config]]
            [infra.atom-db :refer [new-db]]
            [clojure.test :refer :all]))

(def config
  (load-config :test))

(def ^:dynamic *system*)

(defn once-fixture [tests]
  (alter-var-root #'*system* (constantly
                              (component/start (unit-test-system config))))
  (tests)
  (alter-var-root #'*system* component/stop))

(defn each-fixture [tests]
  (remove-all (:store *system*))
  (tests)
  (remove-all (:store *system*)))

(deftest test-new-store
  (testing "new store must be empty."
    (is (empty? (get-votes (:store *system*))))))

(deftest test-using-store
  (testing "got store with correctly added votes."
    (add-vote (:store *system*) {:candidate "sister"})
    (add-vote (:store *system*) {:candidate "brother"})
    (is (every? :candidate (get-votes (:store *system*))))
    (is (= 2 (count (get-votes (:store *system*)))))))

(use-fixtures :once once-fixture)
(use-fixtures :each each-fixture)
