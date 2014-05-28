(ns vivo_widgets_reporter.citations-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)
                    ])
  (:require [cemerick.cljs.test :as t]
            [vivo_widgets_reporter.citations :as src]
            )
  )

(deftest journal-citation-without-volume
  (let [data {:vivoType "http://purl.org/ontology/bibo/AcademicArticle",
              :label "The Article Title",
              :attributes {:startPage "259",
                           :publishedIn "The Journal",
                           :year "2013-01-01T00:00:00",
                           :authorList "Doe, J",
                           :endPage "277"}}]
    (is (= (src/pub-citation data)
        "Doe, J. The Article Title. The Journal (2013): 259-277.")
        )))

;(deftest journal-citation-with-volume
  ;(let [data {:vivoType "http://purl.org/ontology/bibo/AcademicArticle",
              ;:label "The Article Title",
              ;:attributes {:startPage "259",
                           ;:publishedIn "The Journal",
                           ;:year "2013-01-01T00:00:00",
                           ;:authorList "Doe, J",
                           ;:volume "1"
                           ;:endPage "277"}}]
    ;(is (= (src/pub-citation data)
        ;"Doe, J. The Article Title. The Journal 1 (2013): 259-277.")
        ;)))
