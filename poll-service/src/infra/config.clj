(ns infra.config
  (:require [clojure.java.io :as io]
            [aero.core :as aero]))

(defn load-config [profile]
  (let [file (io/resource "config.edn")]
    (aero/read-config file {:profile profile})))
