(ns core
  (:require [com.stuartsierra.component :as component]
            [infra.config :refer [load-config]]
            [system :refer [profile new-system]])
  (:gen-class))

(defn -main [& args]
  (-> (profile) load-config new-system component/start))
