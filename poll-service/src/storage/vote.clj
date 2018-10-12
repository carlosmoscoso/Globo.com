(ns storage.vote
  (:require [com.stuartsierra.component :as component]
            [infra.redis :as redis]))

(defprotocol VoteStore
  (add-vote   [this v])
  (get-votes  [this])
  (remove-all [this]))

(defrecord InMemoryStore [atom-db]
  component/Lifecycle

  (start [this]
    (assoc this :atom-db atom-db))

  (stop [this]
    (assoc this :atom-db nil))
  
  VoteStore
  
  (add-vote [_ v]
    (let [db (:db atom-db)]
      (swap! db update-in ["votes"] conj v)))
  
  (get-votes [_]
    (let [db (:db atom-db)]
      (get @db "votes")))

  (remove-all [_]
    (let [db (:db atom-db)]
      (reset! db {}))))

(defrecord RedisStore [redis]
  component/Lifecycle

  (start [this]
    this)

  (stop [this]
    this)

  VoteStore
  
  (add-vote [_ v]
    (redis/rpush redis "votes" v))
  
  (get-votes [_]
    (redis/lrange redis "votes" 0 (redis/llen redis "votes")))

  (remove-all [_]
    (redis/flushdb redis)))

(defmulti new-store :type)

(defmethod new-store :mem
  [_]
  (map->InMemoryStore {}))

(defmethod new-store :redis
  [_]
  (map->RedisStore {}))
