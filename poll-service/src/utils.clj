(ns utils
  (:require [clj-time.format :refer [formatters parse unparse]]
            [clj-time.core :refer [year month day hour]]))

(def now clj-time.core/now)

(defn parse-date [string]
  (parse (formatters :date-time-no-ms) string))

(defn reset-minutes [date]
  (apply clj-time.core/date-time ((juxt year month day hour) date)))

(defn to-string [date]
  (unparse (formatters :date-time-no-ms) date))

(def reset-minutes-str
  (comp to-string
        reset-minutes
        parse-date))
