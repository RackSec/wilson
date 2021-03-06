(ns wilson.dom-test
  (:require [wilson.dom :as d]
            [cljs.test :refer-macros [is are deftest testing]]
            [cljs.core.match :refer-macros [match]]
            [reagent.core :as r]
            [wilson.core-test :refer [rflush with-mounted-component rstr isClient add-test-div]]))

(defn click-dom-el
  "Dispatch click mouse-event (Web API) on given element."
  [el]
  (let [mouse-event (.createEvent js/document "MouseEvents")]
    (.initMouseEvent mouse-event "click" true true js/window 1 0 0)
    (.dispatchEvent el mouse-event)))

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

(deftest get-all-keys-test
  (testing "getting all keys from nested map"
    (is (= (d/get-all-keys {:a {:b "abc"}
                            :c 123
                            :d {:e {:f "abc"}}})
           [[:a :b] [:c] [:d :e :f]]))))

(deftest sort-rows-test
  (let [rows [{:a 1 :b "B" :c {:x "abc"}}
              {:a 4 :b "A" :c :y}
              {:a 3 :b "D" :c {:a "def"}}
              {:a 2 :b "C" :c :z}]
        sort-fns {:default (fn [k rows] (sort-by k rows))
                  :b (fn [k rows] (reverse (sort-by k rows)))}]
    (testing "default order fuction"
      (is (= (d/sort-rows rows sort-fns :a)
             [{:a 1 :b "B" :c {:x "abc"}}
              {:a 2 :b "C" :c :z}
              {:a 3 :b "D" :c {:a "def"}}
              {:a 4 :b "A" :c :y}])))
    (testing "per-key order fuction"
      (is (= (d/sort-rows rows sort-fns :b)
             [{:a 3 :b "D" :c {:a "def"}}
              {:a 2 :b "C" :c :z}
              {:a 1 :b "B" :c {:x "abc"}}
              {:a 4 :b "A" :c :y}])))))

(deftest table-test
  (testing "simple table"
    (is (= (d/table (d/prepare-keys [:a-key :some-key :some-other-key])
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
           [:table {:class "table"}
            [:thead
             [:tr
              [:th {} "A key"]
              [:th {} "Some key"]
              [:th {} "Some other key"]]]
            [:tbody
             [:tr {}
              [:td {} (d/label "warning" "h")]
              [:td {} "i"]
              [:td {} "j"]]
             [:tr {}
              [:td {} "p"]
              [:td {} (d/label "warning" "q")]
              [:td {} "r"]]
             [:tr {}
              [:td {} "x"]
              [:td {} "y"]
              [:td {} (d/label "warning" "z")]]]])))
  (testing "table with nested data"
    (is (= (d/table (d/prepare-keys [:a-key :some-key [:a :b :c]])
                    [{:a-key (d/label "warning" "h")
                      :some-key "i"
                      :hidden "hidden"
                      :a {:b {:c 123}}}
                     {:a-key "p"
                      :some-key (d/label "warning" "q")
                      :hidden "hidden"
                      :a {:b {:c 456}}}
                     {:a-key "x"
                      :some-key "y"
                      :hidden "hidden"
                      :a {:b {:c "abc"}}}])
           [:table {:class "table"}
            [:thead
             [:tr
              [:th {} "A key"]
              [:th {} "Some key"]
              [:th {} "A.b.c"]]]
            [:tbody
             [:tr {}
              [:td {} (d/label "warning" "h")]
              [:td {} "i"]
              [:td {} "123"]]
             [:tr {}
              [:td {} "p"]
              [:td {} (d/label "warning" "q")]
              [:td {} "456"]]
             [:tr {}
              [:td {} "x"]
              [:td {} "y"]
              [:td {} "abc"]]]])))
  (testing "table with per-row clases"
    (is (= (d/table (d/prepare-keys [:a-key :some-key :some-other-key])
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
                    {:row->attrs (fn [{:keys [some-other-key]}]
                                   (condp = some-other-key
                                     "j" {:class "warning"}
                                     "r" {:class "success"}
                                     {:class "info"}))})
           [:table {:class "table"}
            [:thead
             [:tr
              [:th {} "A key"]
              [:th {} "Some key"]
              [:th {} "Some other key"]]]
            [:tbody
             [:tr {:class "warning"}
              [:td {} (d/label "warning" "h")]
              [:td {} "i"]
              [:td {} "j"]]
             [:tr {:class "success"}
              [:td {} "p"]
              [:td {} (d/label "warning" "q")]
              [:td {} "r"]]
             [:tr {:class "info"}
              [:td {} "x"]
              [:td {} "y"]
              [:td {} (d/label "warning" "z")]]]])))
  (testing "table with per-td classes"
    (is (= (d/table (d/prepare-keys [:a :b :c])
                    [{:a 1 :b 2 :c 3}
                     {:a 8 :b 9 :c 4}
                     {:a 7 :b 6 :c 5}]
                    {:cell-k->attrs (fn [k]
                                      {:class (d/describe-key k)})})
           [:table {:class "table"}
            [:thead
             [:tr
              [:th {} "A"]
              [:th {} "B"]
              [:th {} "C"]]]
            [:tbody
             [:tr {}
              [:td {:class "A"} "1"]
              [:td {:class "B"} "2"]
              [:td {:class "C"} "3"]]
             [:tr {}
              [:td {:class "A"} "8"]
              [:td {:class "B"} "9"]
              [:td {:class "C"} "4"]]
             [:tr {}
              [:td {:class "A"} "7"]
              [:td {:class "B"} "6"]
              [:td {:class "C"} "5"]]]])))
  (testing "boolean values are strings"
    (is (= (d/table (d/prepare-keys [:a :b :c])
                    [{:a "abc"
                      :b false
                      :c true}])
           [:table {:class "table"}
            [:thead
             [:tr
              [:th {} "A"]
              [:th {} "B"]
              [:th {} "C"]]]
            [:tbody
             [:tr {}
              [:td {} "abc"]
              [:td {} "false"]
              [:td {} "true"]]]]))))

(deftest sorted-table-test
  (let [state (r/atom {})
        rows [{:a 1 :b "B" :c -6}
              {:a 4 :b "A" :c 2}
              {:a 3 :b "D" :c 4}]
        expected-a-rows ["1" "4" "3"]
        ks (d/prepare-keys [:a :b :c])
        component (d/sorted-table
                   ks
                   rows
                   state)
        [table-headers table-rows] (match (component ks rows state)
                                     [:table _
                                      [:thead
                                       [:tr & table-headers]]
                                      [:tbody & table-rows]]
                                     [table-headers table-rows])]
    (doseq [[[_ _ [_ _ a]] expected]
            (map vector table-rows expected-a-rows)]
      (is (= a expected)))
    (with-mounted-component [component ks rows state]
      (fn [c div]
        (let [first-th (.querySelector div "th:first-of-type")
              first-th-class (.-className first-th)
              click #(do (click-dom-el first-th) (rflush))]
          (is (empty? (.-className first-th)))
          (click)
          (testing "clicking on header triggers sorting"
            (is (= (.-className first-th) "asc")))
          (click)
          (testing "clicking second time changes sorting order"
            (is (= (.-className first-th) "desc"))))))

    (with-mounted-component [d/sorted-table ks rows state {:k->attrs
                                                           (fn [k]
                                                             {:class "some-class"})}]
      (fn [c div]
        (let [first-th (.querySelector div "th:first-of-type")
              click #(do (click-dom-el first-th) (rflush))]
          (testing "first th should have class added"
            (is (= (.-className first-th) "some-class")))
          (click)
          (testing "on-click should still work"
            (is (= (.-className first-th) "asc some-class"))))))))

(deftest button-test
  (let [t "Some text"
        f (constantly nil)]
    (is (= (d/button t f)
           [:a {:class "btn btn-default" :on-click f} t]))
    (is (= (d/button :primary t f)
           [:a {:class "btn btn-primary" :on-click f} t]))))

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
           [:a {:href "/xyzzy" :class "colorful panel"}])))
  (testing "with-class on form-2 component"
    (let [state (r/atom {})
          rows [{:a 1 :b "B" :c -6}
                {:a 4 :b "A" :c 2}
                {:a 3 :b "D" :c 4}]
          ks (d/prepare-keys [:a :b :c])]
      (is (= (rstr (d/with-class "some-class" [d/sorted-table ks rows state]))
             (rstr [:table {:class "table some-class"}
                    [:thead
                     [:tr
                      [:th "A"]
                      [:th "B"]
                      [:th "C"]]]
                    [:tbody
                     [:tr
                      [:td "1"]
                      [:td "B"]
                      [:td "-6"]]
                     [:tr
                      [:td "4"]
                      [:td "A"]
                      [:td "2"]]
                     [:tr
                      [:td "3"]
                      [:td "D"]
                      [:td "4"]]]]))))))

(deftest icon-test
  (is (= (d/icon "download")
         [:span {:class "glyphicon glyphicon-download"
                 :aria-hidden true}])))

(deftest affix-test
  (when isClient
    (let [div (add-test-div "_affix_test")
          comp (r/render
                [d/affix [:p {:style {:height 9000}} "Some content"]]
                div)
          state (r/state comp)]
      (is (false? (:affix @state)))
      (is (:waypoint-instance @state))
      (.removeChild (.-body js/document) div))))

(deftest modal-test
  (let [state (r/atom {})]
    (with-mounted-component [:div
                             [d/modal-window state]
                             [d/modal-button "Modal title"
                              "Modal content"
                              "Click me!"
                              state
                              {:id "toggle"}]]
      (fn [c div]
        (let [button (.querySelector div "#toggle")
              modal-state (:wilson-modal @state)]
          (is (nil? (:title modal-state)))
          (is (nil? (:content modal-state)))
          (is (nil? (:show modal-state)))
          (click-dom-el button)
          (is (true? (get-in @state [:wilson-modal :show?])))
          (is (= (get-in @state [:wilson-modal :title]) "Modal title"))
          (is (= (get-in @state [:wilson-modal :content]) "Modal content"))
          (is (= (.-text button) "Click me!")))))))
