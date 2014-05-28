(ns vivo_widgets_reporter.citations
  (:require [clojure.string :as string])
  )

(defn journal-citation [{label :label {:keys [authorList
                                              publishedIn
                                              volume
                                              issue
                                              year
                                              startPage
                                              endPage]} :attributes}]
  (str authorList ". "
       label ". "
       publishedIn " "
       volume
       (if issue (str ", no. " issue))
       (if (or volume issue) " ")
       "(" (string/join (take 4 year )) "): "
       startPage "-" endPage 
       "."
       )
  )

(defn book-citation [{:keys [label attributes]}]
  (str (:authorList attributes) ". "
       label ". "
       (string/join (take 4 (:year attributes))) "."
       )
  )

(defn pub-citation [{:keys [vivoType] :as json}]
  (cond
    (re-matches #".*AcademicArticle" vivoType) (journal-citation json)
    (re-matches #".*Book" vivoType) (book-citation json)
    :else "no such type\n\n"
    )
  )

