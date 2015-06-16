(ns shrieker.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [shrieker.dom-test]))

(doo-tests 'shrieker.dom-test)
