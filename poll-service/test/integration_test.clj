(ns integration-test
  (:require [com.stuartsierra.component :as component]
            [system :refer [integration-test-system]]
            [server.core :refer [app]]
            [storage.vote :refer [new-store remove-all]]
            [infra.config :refer [load-config]]
            [infra.atom-db :refer [new-db]]
            [ring.mock.request :as mock]
            [clojure.test :refer :all]))

(def config
  (load-config :prod))

(def ^:dynamic *system*)

(defn once-fixture [tests]
  (alter-var-root #'*system* (constantly
                              (component/start (integration-test-system config))))
  (tests)
  (alter-var-root #'*system* component/stop))

(defn each-fixture [tests]
  (remove-all (:store *system*))
  (tests)
  (remove-all (:store *system*)))

(deftest ^:integration test-redis-storage
  (let [routes (app (:store *system*) nil)]
    (testing "POST /poll, in which the request is fulfilled."
      (are [expected request] (= expected (:status (routes request)))
        
        202 (-> (mock/request :post "/poll")
                (mock/content-type "application/x-www-form-urlencoded")
                (mock/body {:candidate "Brother" :token "invalid"}))
        
        202 (-> (mock/request :post "/poll")
                (mock/content-type "application/x-www-form-urlencoded")
                (mock/body {:candidate "Sister" :token "invalid"}))
        
        202 (-> (mock/request :post "/poll")
                (mock/content-type "application/x-www-form-urlencoded")
                (mock/body {:candidate "Brother" :token "invalid"}))))
    
    (testing "GET /votes, in which the request is fulfilled."
      (let [resp (routes (mock/request :get "/votes"))
            body-as-json (cheshire.core/parse-string (:body resp))
            stats (get-in body-as-json ["stats" "by_participant"])]
        (is (= 200 (:status resp)))
        (is (= 3 (get body-as-json "total")))
        (is (= {"Brother" 2 "Sister" 1} stats))))))

(deftest ^:integration test-captcha-verification
  (let [routes (apply app ((juxt :store :captcha) *system*))]
    (testing "POST /poll, in which the request is fulfilled."
      (are [expected request] (= expected (:status (routes request)))

        202 (-> (mock/request :post "/poll")
                (mock/content-type "application/x-www-form-urlencoded")
                (mock/body {:candidate "Sister" :token "invalid"}))

        202 (-> (mock/request :post "/poll")
                (mock/content-type "application/x-www-form-urlencoded")
                (mock/body {:candidate "Brother" :token "invalid"}))))

    (testing "GET /votes, in which the request is fulfilled."
      (let [resp (routes (mock/request :get "/votes"))
            body-as-json (cheshire.core/parse-string (:body resp))
            stats (get-in body-as-json ["stats" "by_participant"])]
        (is (= 200 (:status resp)))
        (is (zero? (get body-as-json "total")))
        (is (empty? stats))))))

(use-fixtures :once once-fixture)
(use-fixtures :each each-fixture)
