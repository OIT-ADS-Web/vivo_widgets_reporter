(ns vivo_widgets_reporter.citations-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)
                    ])
  (:require [cemerick.cljs.test :as t]
            [vivo_widgets_reporter.citations :as src]
            )
  )

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(def base-journal {:vivoType "http://purl.org/ontology/bibo/AcademicArticle",
                   :label "The Article Title",
                   :attributes {:startPage "259",
                                :publishedIn "The Journal",
                                :year "2013-01-01T00:00:00",
                                :authorList "Doe, J",
                                :endPage "277"}})

(deftest journal
  (let [data base-journal]
    (is (= (src/pub-citation data)
        "Doe, J. The Article Title. The Journal (2013): 259-277.")
        )))

(deftest other-article-behaves-like journal
  (let [data (assoc base-journal :vivoType "http://purl.org/ontology/bibo/OtherArticle")]
    (is (= (src/pub-citation data)
        "Doe, J. The Article Title. The Journal (2013): 259-277."))))

(deftest journal-with-published-by
  (let [data (-> base-journal
                 (dissoc-in [:attributes :publishedIn])
                 (assoc-in  [:attributes :publishedBy] "Another Publisher")
                 )]
    (is (= (src/pub-citation data)
        "Doe, J. The Article Title. Another Publisher (2013): 259-277."))))

(deftest journal-citation-with-volume
  (let [data (assoc-in base-journal [:attributes :volume] "1")]
    (is (= (src/pub-citation data)
        "Doe, J. The Article Title. The Journal 1 (2013): 259-277.")
        )))

(deftest journal-citation-with-issue
  (let [data (-> base-journal
                 (assoc-in [:attributes :volume] "1")
                 (assoc-in [:attributes :issue] "20"))]
    (is (= (src/pub-citation data)
           "Doe, J. The Article Title. The Journal 1, no. 20 (2013): 259-277.")
        )))

(deftest journal-without-pages
  (let [data (-> base-journal
                 (dissoc-in [:attributes :startPage])
                 (dissoc-in [:attributes :endPage]))]
    (is (= (src/pub-citation data)
           "Doe, J. The Article Title. The Journal (2013)."))))

(def base-book {:vivoType "http://purl.org/ontology/bibo/Book",
                :label "The Book Title",
                :attributes {:year "2007-01-01T00:00:00",
                             :authorList "Doe, J"}})

(deftest book-citation
  (let [data base-book]
    (is (= (src/pub-citation data)
           "Doe, J. The Book Title. 2007."))))

(deftest book-with-publisher
  (let [data (assoc-in base-book [:attributes :publishedBy] "The Publisher")]
    (is (= (src/pub-citation data)
           "Doe, J. The Book Title. The Publisher, 2007."))))

(deftest book-without-author-list
  (let [data (dissoc-in base-book [:attributes :authorList])]
    (is (= (src/pub-citation data)
           "The Book Title. 2007."))))

(deftest book-with-blank-author-list
  (let [data (assoc-in base-book [:attributes :authorList] "")]
    (is (= (src/pub-citation data)
           "The Book Title. 2007."))))

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
