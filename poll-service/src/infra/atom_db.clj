(ns infra.atom-db
  (:require [com.stuartsierra.component :as component]))

(defrecord AtomDb []
  component/Lifecycle

  (start [this]
    (assoc this :db (atom {})))

  (stop [this]
    (assoc this :db nil)))

(defn new-db []
  (AtomDb.))
