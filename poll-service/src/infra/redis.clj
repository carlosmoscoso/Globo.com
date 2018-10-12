(ns infra.redis
  (:require [com.stuartsierra.component :as component]
            [taoensso.carmine :as car]))

(defprotocol IRedis
  (llen [this k])
  (rpush [this k value])
  (lrange [this k start stop])
  (flushdb [this]))

(defrecord Redis [uri conn]
  component/Lifecycle
  
  (start [this]
    (assoc this :conn {:spec {:uri uri}}))
  
  (stop [this]
    (assoc this :conn nil))

  IRedis

  (llen [_ k]
    (car/wcar conn (car/llen k)))
  
  (rpush [_ k value]
    (car/wcar conn (car/rpush k value)))

  (lrange [_ k start stop]
    (car/wcar conn (car/lrange k start stop)))

  (flushdb [_]
    (car/wcar conn (car/flushdb))))

(defn new-redis [config]
  (map->Redis config))
