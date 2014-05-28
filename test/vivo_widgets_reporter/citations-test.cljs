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

(deftest journal-citation-without-volume
  (let [data base-journal]
    (is (= (src/pub-citation data)
        "Doe, J. The Article Title. The Journal (2013): 259-277.")
        )))

(deftest journal-citation-with-volume
  (let [data (assoc-in base-journal [:attributes :volume] "1")]
    (is (= (src/pub-citation data)
        "Doe, J. The Article Title. The Journal 1 (2013): 259-277.")
        )))

(deftest journal-citation-with-issue
  (let [data (assoc-in (assoc-in base-journal [:attributes :volume] "1")
                       [:attributes :issue] "20")]
    (is (= (src/pub-citation data)
           "Doe, J. The Article Title. The Journal 1, no. 20 (2013): 259-277.")
        )))

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
    (println data)
    (is (= (src/pub-citation data)
           "The Book Title. 2007."))
    )
  )
