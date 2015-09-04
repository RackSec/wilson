(ns wilson.grid-test
  (:require [wilson.grid :as g]
            [cljs.test :refer-macros [deftest is]]))

(deftest row-test
  (is (= (g/row [:span "Hi"])
         [:div {:class "row"}
          [:span "Hi"]])
      "one element"))

(deftest centered-test
  (is (= (g/centered "sm" 6 [:div])
         [:div {:class "col-sm-6 col-sm-offset-3"}])
      "centered elem of width 6")
  (is (= (g/centered "sm" 8 [:div])
         [:div {:class "col-sm-8 col-sm-offset-2"}])
      "centered elem of width 8")
  (is (= (g/centered 6 [:div])
         [:div {:class "col-md-6 col-md-offset-3"}])
      "default window class is ``md''"))
