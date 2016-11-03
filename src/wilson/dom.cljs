(ns wilson.dom
  "Tools for building DOMs."
  (:require [wilson.utils :refer [capitalize]]
            [clojure.string :as string]
            [cljsjs.waypoints]
            [wilson.react-bootstrap :refer [modal modal-header modal-body
                                            modal-footer]]
            [reagent.core :as r]))

(declare merge-attrs)

(defn describe-key
  "Returns a key in a human-readable form."
  [k]
  (if (keyword? k)
    (capitalize k)
    (::descr (meta k))))

(defn prepare-keys
  "Prepares keys for use with wilson.dom/table.

  `ks` should be a collection of singular keys or vectors of keys pointing at
  nested data. Vectors are interpreted as paths to the relevant data as per
  `get-in`.  They also come with ::descr metadata, so they will be displayed
  as a dotted path by default, e.g.: [:a :b :c] will become 'A.b.c'."
  [ks]
  (map (fn [k]
         (if (vector? k)
           (let [f #(get-in % k)
                 descr (capitalize (string/join "." (map name k)))]
             (vary-meta f assoc ::descr descr))
           k))
       ks))

(defn get-all-keys
  "Returns a list of all available keys in a map. Nested branches will
  be represented as paths to the relevant data (as per `get-in`)."
  [m]
  (when (map? m)
    (mapcat (fn [[k v]]
              (let [nested (->> (get-all-keys v)
                                (filter seq)
                                (map (partial into [k])))]
                (if (seq nested) nested [[k]])))
            m)))

(defn sort-rows
  "Sorts rows `by-key` using corresponding sorting function.
  If `by-key` is not found in `sort-fns` map then `:default` will be used."
  [rows sort-fns by-key]
  (if (contains? sort-fns by-key)
    ((by-key sort-fns) by-key rows)
    ((:default sort-fns) by-key rows)))

(defn parse-td-data
  "Return booleans (and other data other than actual strings or vectors) as
  strings.  Used in table to make sure everything is displayed in the browser."
  [x]
  (if-not (or (vector? x) (string? x) (nil? x))
    (pr-str x)
    x))

(defn table
  "Creates a table displaying the keys in the given rows of data.
  Accepts singular keys or vectors of keys pointing at nested data."
  ([ks rows {:keys [row->attrs k->attrs cell-k->attrs describe-key
                    prepare-keys data->hiccup]
             :or {row->attrs (constantly {})
                  k->attrs (constantly {})
                  cell-k->attrs (constantly {})
                  describe-key describe-key
                  data->hiccup parse-td-data}}]
   [:table {:class "table"}
    [:thead
     (into [:tr]
           (for [k ks]
             [:th (k->attrs k)
              (describe-key k)]))]
    (into [:tbody]
          (for [row rows]
            (into [:tr (row->attrs row)]
                  (for [k ks]
                    (let [td-data (k row)]
                      [:td (cell-k->attrs k)
                       (data->hiccup td-data)])))))])
  ([ks rows]
   (table ks rows {})))

(defn sorted-table
  "Returns a sortable table (using `wilson.dom/table`) with rows sorted using
  `wilson.dom/sort-rows`. Table headers will have an on-click handler that
  will change the sorting key and/or order (those values are saved under
  unique names in the passed state), as well as a class of `asc` or `desc`,
  when table is ordered by corresponding key (refer to
  `resources/public/site.css` for example table styling). You can pass the
  same options  map that `wilson.dom/table` would accept. In addition, you can
  extend options map with `:sort-fns` - its value will get passed directly to
  `wilson.dom/sort-rows."
  ([ks rows state opts]
   (let [sort-key-id (or (:sort-key-id opts) (gensym "wilson-sort-key"))
         sort-order-id (or (:sort-order-id opts) (gensym "wilson-sort-order"))]
     (swap! state merge {sort-order-id :asc})
     (fn [ks rows state opts]
       (let [state-deref @state
             sort-fns (or (:sort-fns opts)
                          {:default (fn [k rows]
                                      (if (= (sort-order-id state-deref) :asc)
                                        (sort-by k rows)
                                        (reverse (sort-by k rows))))})
             maybe-sorted-rows (if (sort-key-id state-deref)
                                 (sort-rows rows sort-fns (sort-key-id state-deref))
                                 rows)
             get-new-order
             (fn [state k]
               (let [swap-order {:asc :desc :desc :asc}]
                 (merge state
                        {sort-key-id k
                         sort-order-id (if (= (sort-key-id state) k)
                                         (swap-order (sort-order-id state))
                                         :asc)})))
             update-state-order #(swap! state get-new-order %)
             update-opts (update opts :k->attrs
                                 (fn [user-k->attrs]
                                   (fn [k]
                                     (let [u (when user-k->attrs (user-k->attrs k))
                                           sort-class (when (= k (sort-key-id state-deref))
                                                        (name (sort-order-id state-deref)))]
                                       (merge-attrs {:class sort-class
                                                     :on-click #(update-state-order k)}
                                                    u)))))]
         (table ks maybe-sorted-rows update-opts)))))
  ([ks rows state]
   (sorted-table ks rows state {})))

(defn label
  "Creates a pretty Bootstrap label."
  [style text]
  [:span
   {:class (str "label label-" style)}
   text])

(defn alert
  "Creates a Bootstrap alert."
  [style text]
  [:div {:class (str "alert alert-" (name style))
         :role "alert"}
   text])

(defn ^:private many-elems
  [maybe-elems]
  (if (keyword? (first maybe-elems))
    [maybe-elems]
    maybe-elems))

(defn panel
  "Creates a Bootstrap panel."
  [header contents]
  [:div {:class "panel panel-default"}
   (into [:div {:class "panel-heading"}] (many-elems header))
   (into [:div {:class "panel-body"}] (many-elems contents))])

(defn button
  "Creates a Bootstrap-styled button."
  ([text on-click]
   (button :default text on-click))
  ([style text on-click]
   [:a {:class (str "btn btn-" (name style)) :on-click on-click} text]))

(defn merge-attrs
  "Merge DOM element attributes.

  Classes get merged. For other attrs, last one wins."
  [& ms]
  (let [merged (apply merge ms)
        classes (string/join " " (keep :class ms))]
    (if (seq classes)
      (assoc merged :class classes)
      merged)))

(defn with-attrs
  "Adds some attrs to an element."
  [attrs elem]
  (update elem 1 merge-attrs attrs))

(defn with-class
  "Adds a class to a component."
  [cls component]
  (let [parse-f2c (fn [render-fn & outer-props]
                    (let [outer-comp (apply render-fn outer-props)]
                      (fn [_ & inner-props]
                        (let [inner-comp (apply outer-comp inner-props)]
                          (with-attrs {:class cls} inner-comp)))))]
    (if (keyword? (first component))
      (with-attrs {:class cls} component)
      (into [parse-f2c] component))))

(defn icon
  "Creates a Glyphicon.

  `type` should be a glyphicon name."
  [type]
  [:span {:class (str "glyphicon glyphicon-" (name type))
          :aria-hidden true}])

(defn affix-render
  [child]
  (let [node (r/current-component)
        component-state (r/state node)]
    [:div {:class (when (@component-state :affix) "affix")}
     child]))

(def affix
  "Creates a wrapper for your component that will have a `affix`  class when
  viewport is scrolled past the wrapper."
  (r/create-class
   {:get-initial-state (fn [] (r/atom {:affix false}))
    :component-did-mount
    (fn [this]
      (let [node (r/dom-node this)
            component-state (r/state this)
            scroll-handler (fn [direction]
                             (if (= direction "up")
                               (swap! component-state assoc :affix false)
                               (swap! component-state assoc :affix true)))
            waypoint-instance (js/Waypoint.
                               #js {:element node :handler scroll-handler})]
        (swap! component-state assoc :waypoint-instance waypoint-instance)))
    :component-will-unmount
    (fn [this]
      (let [node (r/dom-node this)
            component-state (r/state this)]
        (.destroy (:waypoint-instance @component-state))))
    :reagent-render affix-render}))

(defn modal-window
  "Modal window used by modal-button component. Values for the modal will be
  stored in the state under `:wilson-modal`."
  [state]
  (let [modal-state (:wilson-modal @state)
        show? (:show? modal-state)
        title (:title modal-state)
        content (:content modal-state)
        close-modal #(swap! state assoc-in [:wilson-modal :show?] false)]
    [modal {:show show? :on-hide close-modal}
     [modal-header title]
     [modal-body content]
     [modal-footer
      [:button.btn.btn-danger {:on-click close-modal}
       "Close"]]]))

(defn modal-button
  "A button that toggles modal-window on and off."
  ([title content btn-text state]
   (modal-button title content btn-text state {}))
  ([title content btn-text state attrs]
   (swap! state merge {:wilson-modal {:title title :content content}})
   (fn [title content btn-text state attrs]
     [:a (merge {:on-click #(swap! state assoc-in [:wilson-modal :show?] true)}
                attrs)
      btn-text])))
