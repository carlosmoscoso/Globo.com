(ns server.core
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.codec :refer [form-decode]]
            [compojure.core :refer [routes ANY]]
            [compojure.route :refer [not-found]]
            [liberator.core :refer [defresource]]
            [storage.vote :refer [add-vote get-votes]]
            [captcha.core :refer [verify?]]
            [domain :refer :all]
            [utils]))

(defresource poll [store captcha]
  :allowed-methods [:post]
  
  :malformed? (fn [{{body :body :as request} :request}]
                (try (let [x (->> (form-decode (slurp body))
                                  (#(for [[k,v] %] [(keyword k) v]))
                                  (into {}))]
                       [(not (valid-vote? x)), {::vote x}])
                     (catch Exception _ true)))
  
  :allowed? (comp not empty? :token ::vote)

  :available-media-types ["application/json"]

  :post! (fn [{{token :token :as vote} ::vote}]
           (let [t (utils/to-string (utils/now))]
             (future
               (when (or (nil? captcha) (verify? captcha token))
                 (add-vote store (-> vote (dissoc token) (assoc :time t)))))))

  :post-enacted? false

  :handle-accepted (constantly
                    (breakdown-by :candidate (get-votes store))))

(defresource votes [store]
  :allowed-methods [:head :get]
  
  :available-media-types ["application/json"]
  
  :handle-ok (constantly
              (let [votes (get-votes store)]
                {:total (count votes)
                 :stats {:by_participant (breakdown-by :candidate votes)
                         :by_hour (vec
                                   (breakdown-by
                                    (comp utils/reset-minutes-str :time)
                                    votes))}})))

(defn app [store captcha]
  (routes (ANY "/poll"  [] (poll store captcha))
          (ANY "/votes" [] (votes store))
          (not-found "No such resource")))

(defrecord WebServer [port conn store captcha]
  component/Lifecycle
  
  (start [this]
    (let [routes (app store captcha)
          options {:port port :join? false}]
      (assoc this :conn (run-jetty routes options))))
  
  (stop [this]
    (.stop conn)
    (.join conn)
    (assoc this :conn nil)))

(defn new-server [config]
  (map->WebServer config))
