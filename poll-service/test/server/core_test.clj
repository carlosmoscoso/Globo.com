(ns server.core-test
  (:require [com.stuartsierra.component :as component]
            [system :refer [unit-test-system]]
            [server.core :refer [app]]
            [storage.vote :refer [new-store]]
            [infra.config :refer [load-config]]
            [infra.atom-db :refer [new-db]]
            [ring.mock.request :as mock]
            [clojure.test :refer :all]))

(def config
  (load-config :test))

(def ^:dynamic *system*)

(defn once-fixture [tests]
  (alter-var-root #'*system* (constantly
                              (component/start (unit-test-system config))))
  (tests)
  (alter-var-root #'*system* component/stop))

(deftest test-route-endpoints
  (let [routes (app (:store *system*) nil)]
    (testing "POST /poll, in which the client seems to have erred."
      (are [expected request] (= expected (:status (routes request)))

        400 (-> (mock/request :post "/poll")
                (mock/content-type "invalid/type"))

        400 (-> (mock/request :post "/poll")
                (mock/content-type "application/json")
                (mock/body "Invalid payload"))))

    (testing "POST /poll, where the site key was not sent by client."
      (are [expected request] (= expected (:status (routes request)))

        403 (-> (mock/request :post "/poll")
                (mock/content-type "application/x-www-form-urlencoded")
                (mock/body {:candidate "Brother"}))))

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

(use-fixtures :once once-fixture)
