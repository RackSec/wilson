(ns shrieker.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [wilson.dom-test]))

(doo-tests 'wilson.dom-test)
