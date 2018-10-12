(ns system
  (:require [com.stuartsierra.component :as component]
            [infra.atom-db :as atom-db]
            [infra.redis :as redis]
            [storage.vote :as vote-store]
            [captcha.core :as recaptcha]
            [server.core :as server]))

(defn profile
  "Returns the system profile."
  []
  {:post [(contains? #{:dev :prod :test} %)]}
  (-> (or (System/getenv "ENV") "dev")
      (.toLowerCase)
      keyword))

(defn new-system
  "Instantiates all system components."
  [{:keys [redis store captcha server]}]
  (component/system-map
   :atom-db (atom-db/new-db)

   :redis (redis/new-redis redis)

   :store (component/using
           (vote-store/new-store store)
           [:atom-db :redis])

   :captcha (recaptcha/new-captcha captcha)

   :web-server (component/using
                (server/new-server server)
                [:store :captcha])))

(defn unit-test-system
  "Instantiates system components for unit testing."
  [{:keys [store]}]
  (component/system-map
   :atom-db (atom-db/new-db)

   :store (component/using
           (vote-store/new-store store)
           [:atom-db])))

(defn integration-test-system
  "Instantiates system components for integration testing."
  [{:keys [redis store captcha]}]
  (component/system-map
   :atom-db (atom-db/new-db)

   :redis (redis/new-redis redis)

   :store (component/using
           (vote-store/new-store store)
           [:atom-db :redis])

   :captcha (recaptcha/new-captcha captcha)))
