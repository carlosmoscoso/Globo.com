(ns domain
  (:require [clojure.spec.alpha :as s]))

(s/def ::candidate string?)

(s/def ::vote (s/keys :req-un [::candidate]))

(defn valid-candidate? [x]
  (s/valid? ::candidate x))

(defn valid-vote? [x]
  (s/valid? ::vote x))

(defn breakdown-by [f votes]
  (frequencies (map f votes)))
