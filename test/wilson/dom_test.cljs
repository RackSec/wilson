(ns wilson.dom-test
  (:require [wilson.dom :as d]
            [cljs.test :refer-macros [is are deftest testing run-tests]]))

(deftest panel-test
  (testing "multi-tag header & contents"
    (is (= (d/panel [[:div {"class" "aside"}]
                     [:h1 "Header"]]
                    [[:div "Contents"]
                     [:div "More contents"]])
           [:div {:class "panel panel-default"}
            [:div {:class "panel-heading"}
             [:div {"class" "aside"}]
             [:h1 "Header"]]
            [:div {:class "panel-body"}
             [:div "Contents"]
             [:div "More contents"]]])))
  (testing "single-tag header & contents"
    (is (= (d/panel [:h1 "Header"] [:div "Contents"])
           [:div {:class "panel panel-default"}
            [:div {:class "panel-heading"}
             [:h1 "Header"]]
            [:div {:class "panel-body"}
             [:div "Contents"]]]))))

(deftest label-test
  (testing "simple label"
    (is (= (d/label "warning" "Alert!")
           [:span {:class "label label-warning"} "Alert!"]))))

(deftest table-test
  (testing "simple table"
    (is (= (d/table [:a-key :some-key :some-other-key]
                    [{:a-key (d/label "warning" "h")
                      :some-key "i"
                      :some-other-key "j"
                      :hidden "hidden"}
                     {:a-key "p"
                      :some-key (d/label "warning" "q")
                      :some-other-key "r"
                      :hidden "hidden"}
                     {:a-key "x"
                      :some-key "y"
                      :some-other-key (d/label "warning" "z")
                      :hidden "hidden"}])
           [:table {:class "table table-hover"}
            [:thead
             [:tr
              [:th "A key"]
              [:th "Some key"]
              [:th "Some other key"]]]
            [:tbody
             [:tr {:class nil}
              [:td (d/label "warning" "h")]
              [:td "i"]
              [:td "j"]]
             [:tr {:class nil}
              [:td "p"]
              [:td (d/label "warning" "q")]
              [:td "r"]]
             [:tr {:class nil}
              [:td "x"]
              [:td "y"]
              [:td (d/label "warning" "z")]]]])))
  (testing "table with per-row classes specified in the input data"
    (is (= (d/table [:a-key :some-key :some-other-key]
                    [{:a-key (d/label "warning" "h")
                      :some-key "i"
                      :some-other-key "j"
                      :hidden "hidden"
                      :wilson/row-class "warning"}
                     {:a-key "p"
                      :some-key (d/label "warning" "q")
                      :some-other-key "r"
                      :hidden "hidden"
                      :wilson/row-class "success"}
                     {:a-key "x"
                      :some-key "y"
                      :some-other-key (d/label "warning" "z")
                      :hidden "hidden"}])
           [:table {:class "table table-hover"}
            [:thead
             [:tr
              [:th "A key"]
              [:th "Some key"]
              [:th "Some other key"]]]
            [:tbody
             [:tr {:class "warning"}
              [:td (d/label "warning" "h")]
              [:td "i"]
              [:td "j"]]
             [:tr {:class "success"}
              [:td "p"]
              [:td (d/label "warning" "q")]
              [:td "r"]]
             [:tr {:class nil}
              [:td "x"]
              [:td "y"]
              [:td (d/label "warning" "z")]]]])))
  (testing "table with per-row clases"
    (is (= (d/table [:a-key :some-key :some-other-key]
                    [{:a-key (d/label "warning" "h")
                      :some-key "i"
                      :some-other-key "j"
                      :hidden "hidden"}
                     {:a-key "p"
                      :some-key (d/label "warning" "q")
                      :some-other-key "r"
                      :hidden "hidden"}
                     {:a-key "x"
                      :some-key "y"
                      :some-other-key (d/label "warning" "z")
                      :hidden "hidden"}]
                    {:row->cls (fn [{:keys [some-other-key]}]
                                 (condp = some-other-key
                                   "j" "warning"
                                   "r" "success"
                                   nil))})
           [:table {:class "table table-hover"}
            [:thead
             [:tr
              [:th "A key"]
              [:th "Some key"]
              [:th "Some other key"]]]
            [:tbody
             [:tr {:class "warning"}
              [:td (d/label "warning" "h")]
              [:td "i"]
              [:td "j"]]
             [:tr {:class "success"}
              [:td "p"]
              [:td (d/label "warning" "q")]
              [:td "r"]]
             [:tr {:class nil}
              [:td "x"]
              [:td "y"]
              [:td (d/label "warning" "z")]]]]))))

(deftest button-test
  (let [t "Some text"
        f (constantly nil)]
    (is (= (d/button t f)
           [:a {:class "btn btn-default" :on-click f} t]))))

(deftest merge-attrs-test
  (testing "last attr wins"
    (is (= (d/merge-attrs {:href "a"} {:href "b"})
           {:href "b"})))
  (testing "classes merge"
    (are [attr-maps result] (= (apply d/merge-attrs attr-maps) result)
      [{:class "a"}]
      {:class "a"}

      [{:class "a"} {:class "b"}]
      {:class "a b"}

      [{:class "a" :href "/xyzzy"} {:class "b"}]
      {:class "a b" :href "/xyzzy"}

      [{:class "a" :href "/xyzzy"} {:class "b" :src "image.png"}]
      {:class "a b" :href "/xyzzy" :src "image.png"})))

(deftest with-attrs-test
  (testing "add some attrs"
    (is (= (d/with-attrs {:width 1 :height 2} [:img])
           [:img {:width 1 :height 2}])))
  (testing "add some attrs with existing attrs"
    (let [elem [:img {:src "img/kitty1.jpg"}]]
      (is (= (d/with-attrs {:width 1 :height 2} elem)
             [:img {:src "img/kitty1.jpg" :width 1 :height 2}]))))
  (testing "merge with existing classes"
    (let [elem [:a {:class "colorful" :href "/xyzzy"}]]
      (is (= (d/with-attrs {:class "panel"} elem)
             [:a {:href "/xyzzy" :class "colorful panel"}])))))

(deftest with-class-test
  (testing "just add a class"
    (is (= (d/with-class "panel" [:a])
           [:a {:class "panel"}])))
  (testing "add a class when other attrs present"
    (is (= (d/with-class "panel" [:a {:href "/xyzzy"}])
           [:a {:href "/xyzzy" :class "panel"}])))
  (testing "merge with existing classes"
    (is (= (d/with-class "panel" [:a {:class "colorful" :href "/xyzzy"}])
           [:a {:href "/xyzzy" :class "colorful panel"}]))))

(deftest icon-test
  (is (= (d/icon "download")
         [:span {:class "glyphicon glyphicon-download"
                 :aria-hidden true}])))
