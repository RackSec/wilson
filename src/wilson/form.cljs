(ns wilson.form
  (:require [wilson.dom :refer [with-attrs with-class]]))

(defn ^:private form-elem
  ([id label input]
   (form-elem id label input {}))
  ([id label input {:keys [label-cls input-container-cls]}]
   [:div {:class "form-group"}
    (with-class label-cls
      [:label {:class "control-label" :for id} label])
    [:div {:class input-container-cls}
     (let [cls (cond
                 (= (first input) :p) "form-control-static"
                 (= (get-in input [1 :type]) "file") "form-control-static"
                 :default "form-control")]
       (with-attrs {:id id :class cls :name id} input))]]))

(defn ^:private horiz-form-elem
  "A horizontal form element, with the label and input horizontally
  next to each other."
  ([id label input]
   (horiz-form-elem id label input {}))
  ([id label input opts]
   (let [opts (merge {:label-cls "col-sm-2"
                      :input-container-cls "col-sm-10"} opts)]
     (form-elem id label input opts))))
