(defproject poll-service "0.1.0"
  :description "Poll service."
  
  :repl-options {:init-ns user}
  
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.taoensso/carmine "2.18.1"]

                 [ring "1.7.0-RC2"]
                 [compojure "1.6.1"]
                 [liberator "0.15.2"]
                 [clj-time "0.14.4"]
                 [clj-http "3.9.1"]
                 [cheshire "5.8.0"]
                 [aero "1.1.3"]]

  :main core

  :uberjar-name "poll-service-standalone.jar"

  :target-path "target/%s"

  :test-selectors {:default (complement :integration)
                   :integration :integration}

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[ring/ring-mock "0.3.2"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [com.stuartsierra/component.repl "0.2.0"]]}

             :uberjar {:aot :all}})
