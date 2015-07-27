(ns wilson.utils
  (:require [clojure.string :as string]))

(def ^:private acronyms
  ["OS" "IP"])

(def ^:private acronym-pattern
  (re-pattern (str "(?i)(" (string/join "|" acronyms) ")")))

(defn capitalize
  "Turns a string or keyword into a capitalized string.

  Acronyms will be properly capitalized.

  Unlike clojure.string/capitalize, works on keywords."
  [s]
  (-> (name s)
      (string/capitalize)
      (string/replace #"[_-]" " ")
      #?(:clj
         (string/replace acronym-pattern (comp string/upper-case first))
         :cljs
         (.replace acronym-pattern string/upper-case))))

(defn str->kw
  "Turns a str into a kw."
  [s]
  (-> s
      (string/lower-case)
      (string/replace #"[_ ]" "-")
      keyword))

(defn kwify-map
  "Turns a map with str keys into one with kw keys.

  Unlike clojure.walk/keywordize-keys, turns spaces into dashes, and
  only works on one level."
  [m]
  (let [kvs (map (fn [[k v]] [(str->kw k) v]) m)]
    (into (empty m) kvs)))

(defn substr?
  "Is sub a substring of super?"
  [super sub]
  #?(:clj (.contains ^String super sub)
     :cljs (not= (.indexOf super sub) -1)))
