(ns wilson.macros)

(defmacro def-reagent-class [var-name js-ns js-name]
  `(def ~var-name
     (r/adapt-react-class
      (aget ~js-ns ~js-name))))
