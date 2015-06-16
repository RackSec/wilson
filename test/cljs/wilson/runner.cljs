(ns shrieker.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [wilson.dom-test]
            [wilson.utils-test]))

(doo-tests 'wilson.dom-test
           'wilson.utils-test)
