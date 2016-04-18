(ns wilson.dom
  "Tools for building DOMs."
  (:require [wilson.utils :refer [capitalize]]
            [clojure.string :as string]))

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

(defn table
  "Creates a table displaying the keys in the given rows of data.
  Accepts singular keys or vectors of keys pointing at nested data."
  ([ks rows {:keys [row->attrs describe-key prepare-keys]
                 :or {row->attrs (constantly {})
                      describe-key describe-key
                      prepare-keys prepare-keys}}]
   (let [ready-keys (prepare-keys ks)]
     [:table {:class "table"}
      [:thead
       (into [:tr]
             (for [k ready-keys]
               [:th (describe-key k)]))]
      (into [:tbody]
            (for [row rows]
              (into [:tr (row->attrs row)]
                    (for [k ready-keys]
                      [:td (k row)]))))]))
  ([ks rows]
   (table ks rows {})))

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
  "Adds a class to an element."
  [cls elem]
  (with-attrs {:class cls} elem))

(defn icon
  "Creates a Glyphicon.

  `type` should be a glyphicon name."
  [type]
  [:span {:class (str "glyphicon glyphicon-" (name type))
          :aria-hidden true}])
