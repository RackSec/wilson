(ns wilson.utils
  (:require [clojure.string :as string]))

(def ^:private acronyms
  ["OS" "IP"])

(def ^:private acronym-pattern
  (re-pattern (str "(?i)(^|\\s)(" (string/join "|" acronyms) ")(\\s|$)")))

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
      (string/replace #"[.]" "")
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

(defn get-or-get-in
  "Will use get when keyword is passed or get-in for vectors"
  [d kw]
  (if (vector? kw)
      (get-in d kw)
      (get d kw)))

(defn kw->dot-notation
  "Turns a vector of keywords into a string with corresponding
  dot-notation (e.g.: [:a :b :c] becomes \"a.b.c\".
  Keywords are returned using `name` function."
  [kw]
  (if (vector? kw)
      (string/join "." (map name kw))
      (name kw)))
