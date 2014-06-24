(ns vivo_widgets_reporter.citations-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)
                    ])
  (:require [cemerick.cljs.test :as t]
            [vivo_widgets_reporter.citations :as src]
            )
  )

(def base-art-work {:vivoType "http://vivo.duke.edu/vivo/ontology/duke-art-extension#MusicalPerformance"
                    :label "The Music of Tin Cans"
                    :attributes {:date_precision "https://vivoweb.org/ontology/core#yearPrecision"
                                 :date "2012-01-05T00:00:00"
                                 :type_description "Musical Performance"
                                 :collaborators "Professor Music Himself"
                                 :role "Conductor" }})

(deftest art-work
  (let [data base-art-work]
    (is (= (src/art-work-citation data)
           "Professor Music Himself. The Music of Tin Cans. Conductor. Musical Performance. 2012."
           ))))

(deftest art-work-with-month-precision
  (let [data (assoc-in base-art-work
                       [:attributes :date_precision]
                       "https://vivoweb.org/ontology/core#yearMonthPrecision")]
    (is (= (src/art-work-citation data)
           "Professor Music Himself. The Music of Tin Cans. Conductor. Musical Performance. 01/2012."
           ))))

(deftest art-work-with-day-precision
  (let [data (assoc-in base-art-work
                       [:attributes :date_precision]
                       "https://vivoweb.org/ontology/core#yearMonthDayPrecision")]
    (is (= (src/art-work-citation data)
           "Professor Music Himself. The Music of Tin Cans. Conductor. Musical Performance. 01/05/2012."
           ))))

(deftest commissioned-art-work
  (let [data (assoc-in base-art-work
                       [:attributes :commissioning_body]
                       "Some Rich Guy")]
    (is (= (src/art-work-citation data)
           "Professor Music Himself. The Music of Tin Cans. Conductor. Musical Performance. Commissioned by Some Rich Guy. 2012."))))
