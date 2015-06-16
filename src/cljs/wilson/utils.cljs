(ns wilson.utils
  (:require [clojure.string :as string]))

(defn capitalize
  "Turns a string or keyword into a capitalized string.

  Unlike clojure.string/capitalize, works on keywords."
  [s]
  (-> (name s)
      (string/capitalize)
      (string/replace #"[_-]" " ")))

(defn str->kw
  "Turns a str into a kw."
  [s]
  (-> s
      (string/lower-case)
      (string/replace #"[_ ]" "-")
      keyword))
