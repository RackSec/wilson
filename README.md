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

### Sortable table component
In addition to a standard table, you can create a table with rows sortable by column user clicks on:

```clojure
(:require [wilson.dom :as d]
          [reagent.core :as r])

(defonce app-state (r/atom {}))

(def ks [:a :b [:c :d]])

(def rows [{:a 1 :b 1 :c {:d "a"}}
          {:a 3 :b 2 :c {:d "b"}}
          {:a 8 :b 3 :c {:d "c"}}
          {:a 2 :b 4 :c {:d "d"}}
          {:a 4 :b 5 :c {:d "e"}}])

(d/sorted-table
 ks
 rows
 app-state)
```

For `app-state` above you can also use [Reagent Session][session].

For expected UX, you should style your sorted table using `asc` and `desc` css classes (added to table headers corresponding to current sorting key). Example styles can be found inside [`site.css`][site-css]



[book]: https://en.wikipedia.org/wiki/By_His_Bootstraps
[bs3]: http://getbootstrap.com/
[cljs]: https://github.com/clojure/clojurescript
[reagent]: https://holmsand.github.io/reagent/
[session]: https://github.com/reagent-project/reagent-utils#reagentsession
[site-css]: https://github.com/RackSec/wilson/blob/master/resources/public/css/site.css
