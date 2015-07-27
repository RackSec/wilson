(ns wilson.utils-test
  (:require
   [wilson.utils :as u]
   #?(:clj [clojure.test :refer [is are deftest]]
      :cljs [cljs.test :refer-macros [is are deftest]])))

(deftest capitalize-test
  (are [in out] (= (u/capitalize in) out)
    ;; Simple case
    "hockey" "Hockey"
    "Hockey" "Hockey"

    ;; With whitespace or (lo)dash
    "playing hockey" "Playing hockey"
    "playing_hockey" "Playing hockey"

    ;; Acronyms at the start
    "os name" "OS name"
    "os_name" "OS name"
    "ip address" "IP address"
    "Ip address" "IP address"

    ;; Acronyms at the end
    "primary ip" "Primary IP"

    ;; Acronyms in the middle
    "primary ip address" "Primary IP address"

    ;; Don't capitalize "acronyms" in the middle of a word
    "apotheosis" "Apotheosis"

    ;; Don't capitalize "acronyms" at the end of a word
    "airship" "Airship"

    :hockey "Hockey"
    :playing-hockey "Playing hockey"
    :os-name "OS name"
    :ip-address "IP address"))

(deftest str->kw-test
  (are [in out] (= (u/str->kw in) out)
    "abc" :abc
    "abc def" :abc-def
    "abc-def" :abc-def
    "abc_def" :abc-def
    "ABC_DEF" :abc-def))

(deftest kwify-map-test
  (are [in out] (= (u/kwify-map in) out)
    {} {}
    {"abc" "abc"} {:abc "abc"}
    {"abc def" "abc def"} {:abc-def "abc def"}))

(deftest substr?-test
  (are [super sub] (u/substr? super sub)
    "" ""
    "x" "x"
    "xyz" "xyz"
    "xyz" "xy"
    "xyz" "z")
  (are [super sub] (not (u/substr? super sub))
    "" "a"
    "abc" "def"))
