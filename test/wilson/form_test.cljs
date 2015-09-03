(ns wilson.form-test
  (:require [wilson.form :as f]
            [cljs.test :refer-macros [is are deftest testing run-tests]]))

(deftest form-elem-tests
  (is (= (f/form-elem "my-id" "The label" [:input {:type "text"}])
         [:div
          {:class "form-group"}
          [:label {:class "col-sm-2 control-label"
                   :for "my-id"}
           "The label"]
          [:div
           {:class "col-sm-10"}
           [:input
            {:type "text"
             :id "my-id"
             :class "form-control"
             :name "my-id"}]]])
      "regular form controls work")
  (is (= (f/form-elem "my-id" "The label" [:input {:type "file"}])
         [:div
          {:class "form-group"}
          [:label {:class "col-sm-2 control-label"
                   :for "my-id"}
           "The label"]
          [:div
           {:class "col-sm-10"}
           [:input
            {:type "file"
             :id "my-id"
             :class "form-control-static"
             :name "my-id"}]]])
      "file controls are static (not decorated)")
  (is (= (f/form-elem "elem" "Label" [:p {} "Not actually input"])
         [:div
          {:class "form-group"}
          [:label
           {:class "col-sm-2 control-label"
            :for "elem"}
           "Label"]
          [:div
           {:class "col-sm-10"}
           [:p
            {:id "elem"
             :class "form-control-static"
             :name "elem"}
            "Not actually input"]]])
      "paragraph elements are static"))

(deftest horiz-form-elem-tests
  (is (= (f/horiz-form-elem "my-id" "The label" [:input {:type "text"}])
         [:div
          {:class "form-group"}
          [:label {:class "col-sm-2 control-label"
                   :for "my-id"}
           "The label"]
          [:div
           {:class "col-sm-10"}
           [:input
            {:type "text"
             :id "my-id"
             :class "form-control"
             :name "my-id"}]]])
      "horizontal form elements have 2-10 grid split by default"))
