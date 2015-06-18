(ns wilson.grid-test
  (:require [wilson.grid :as g]
            [cljs.test :refer-macros [deftest is]]))

(deftest centered-test
  (is (= (g/centered "sm" 6 [:div])
         [:div {:class "col-sm-6 col-sm-offset-3"}])
      "centered elem of width 6")
  (is (= (g/centered 6 [:div])
         [:div {:class "col-md-6 col-md-offset-3"}])
      "default window class is ``md''"))
