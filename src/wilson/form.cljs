(ns wilson.form
  (:require [wilson.dom :refer [with-attrs]]))

(defn ^:private form-elem
  [id label input]
  [:div {:class "form-group"}
   [:label {:class "col-sm-2 control-label" :for id} label]
   [:div {:class "col-sm-10"}
    (let [cls (cond
                (= (first input) :p) "form-control-static"
                (= (get-in input [1 :type]) "file") "form-control-static"
                :default "form-control")]
      (with-attrs {:id id :class cls :name id} input))]])
