(ns vivo_widgets_reporter.test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)
                    ])
  (:require [cemerick.cljs.test :as t])
)

(deftest javascript-allows-div-by-0
  (is (= js/Infinity (/  1  0) (/ (int 1) (int 0))))
  )
