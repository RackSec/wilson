# wilson

[![Build Status](https://travis-ci.org/RackSec/wilson.svg?branch=master)](https://travis-ci.org/RackSec/wilson)
[![Clojars Project](http://clojars.org/wilson/latest-version.svg)](http://clojars.org/wilson)

<img width="25%" src="https://upload.wikimedia.org/wikipedia/en/a/aa/By_His_Bootstraps_ASF_Oct_1941.jpg" align="right">

> Wilson had no reason to suspect that anyone else was in this room;
> he had every reason to expect the contrary. He had locked himself in
> his room for the purpose of completing his thesis in one sustained
> drive. He *had* to--tomorrow was the last day for submission,
> yesterday the thesis had been no more than a title: "An
> investigation into Certain Mathematical Aspects of a Rigor of
> Metaphysics."
>
> [*By His Bootstraps*, Robert A. Heinlein (as Anson MacDonald)][book]

Wilson is an opinionated library to help you use [Twitter Bootstrap 3][bs3] in [Clojurescript][cljs], particularly with [Reagent][reagent].


## Table component

### Simple example
Display simple table with selected columns:

```clojure
(:require [wilson.dom :as d])

(d/table
 (d/prepare-keys [:a [:c :d]])
 [{:a 1 :b 1 :c {:d "a"}}
 {:a 3 :b 2 :c {:d "b"}}
 {:a 8 :b 3 :c {:d "c"}}
 {:a 2 :b 4 :c {:d "d"}}
 {:a 4 :b 5 :c {:d "e"}}])
```

### Sorted rows
Sort table rows using `wilson.dom/sort-rows` by key `:a`:

```clojure
(:require [wilson.dom :as d])

(def rows [{:a 1 :b 1 :c {:d "a"}}
          {:a 3 :b 2 :c {:d "b"}}
          {:a 8 :b 3 :c {:d "c"}}
          {:a 2 :b 4 :c {:d "d"}}
          {:a 4 :b 5 :c {:d "e"}}])

(def sorted-rows (d/sort-rows
                  rows
                  {:default (fn [k rows] (sort-by k rows))}
                  :a))

(d/table
 (d/prepare-keys [:a [:c :d]])
 sorted-rows)
```

### Display all available columns
Instead of typing columns by hand, you can use `wilson.dom/get-all-keys`:

```clojure
(:require [wilson.dom :as d])

(def rows [{:a 1 :b 1 :c {:d "a"}}
          {:a 3 :b 2 :c {:d "b"}}
          {:a 8 :b 3 :c {:d "c"}}
          {:a 2 :b 4 :c {:d "d"}}
          {:a 4 :b 5 :c {:d "e"}}])

(def all-ks (distinct (mapcat d/get-all-keys rows)))

(d/table
 (d/prepare-keys all-ks)
 rows)
```

### Advanced usage
In this example we will add on-click handlers on table headers to sort the rows
(and some CSS classes for styling).

```clojure
(:require [wilson.dom :as d]
          [reagent.core :as r])

(def ks (d/prepare-keys [:a :b [:c :d]]))

(defonce state
  (r/atom {:sort-key (first ks)
           :sort-order :asc}))

(defn table-component
  [state]
  (let [rows [{:a 1 :b 1 :c {:d "a"}}
              {:a 3 :b 2 :c {:d "b"}}
              {:a 8 :b 3 :c {:d "c"}}
              {:a 2 :b 4 :c {:d "d"}}
              {:a 4 :b 5 :c {:d "e"}}]
        sorted-rows (d/sort-rows
                     rows
                     {:default (fn [k rows] (if (= (:sort-order @state) :asc)
                                              (sort-by k rows)
                                              (reverse (sort-by k rows))))}
                     (:sort-key @state))
        get-new-order (fn [state k]
                        (let [sort-key (:sort-key @state)
                              sort-order (:sort-order @state)
                              swap-order {:asc :desc :desc :asc}]
                          {:sort-key k
                           :sort-order (if (= sort-key k)
                                           (swap-order sort-order)
                                           :asc)}))
        update-state-order #(swap! state merge (get-new-order state %))]
   (with-class "sorted-table"
    (d/table
     ks
     sorted-rows
     {:k->attrs (fn [k]
                 {:on-click #(update-state-order k)
                  :class (when (= k (:sort-key @state))
                          (name (:sort-order @state)))})}))))
```

Add some CSS to provide visual cues to the user:

```css
.sorted-table > thead > tr > th {
  position: relative;
  cursor: pointer;
  padding-left: 20px;
}

.sorted-table th:before {
  content: "";
  border-right: 3px solid transparent;
  border-bottom: 3px solid #b6b6b6;
  border-left: 3px solid transparent;
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  margin-top: -2px;
}

.sorted-table th:after {
  content: "";
  border-right: 3px solid transparent;
  border-top: 3px solid #b6b6b6;
  border-left: 3px solid transparent;
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  margin-top: 3px;
}

.sorted-table .asc:before {
  border-bottom-color: #111;
}

.sorted-table .desc:after {
  border-top-color: #111;
}
```

Now when you render `table-component` you'll get a table with live sorting on the front-end.



[book]: https://en.wikipedia.org/wiki/By_His_Bootstraps
[bs3]: http://getbootstrap.com/
[cljs]: https://github.com/clojure/clojurescript
[reagent]: https://holmsand.github.io/reagent/
