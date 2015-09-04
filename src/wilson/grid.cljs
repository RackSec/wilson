(ns wilson.grid
  (:require [wilson.dom :refer [with-class]]
            [clojure.string :refer [join]]))

(defn row
  [& elems]
  (into [:div {:class "row"}] elems))

(defn centered
  "Assigns a width and an offset to the elem so that it is centered
  within its parent, assuming the parent is also a part of a grid."
  ([window-class width elem]
   (let [grid-cls (join "-" ["col" window-class width])
         half (quot (- 12 width) 2)
         offset-cls (join "-" ["col" window-class "offset" half])]
     (with-class (join " " [grid-cls offset-cls]) elem)))
  ([width elem]
   (centered "md" width elem)))
