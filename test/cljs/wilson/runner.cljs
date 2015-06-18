(ns shrieker.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [wilson.dom-test]
            [wilson.utils-test]
            [wilson.form-test]))

(doo-tests 'wilson.dom-test
           'wilson.utils-test
           'wilson.form-test)
