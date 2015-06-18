(ns wilson.grid-test
  (:require [wilson.grid :as g]
            [cljs.test :refer-macros [deftest is]]))

(deftest centered-test
  (is (= (g/centered 6 [:div])
         [:div {:class "col-md-6 col-md-offset-3"}])
      "default window class is ``md''"))
