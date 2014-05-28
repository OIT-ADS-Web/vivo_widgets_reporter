(ns vivo_widgets_reporter.citations
  (:require [clojure.string :as string])
  )

(defn cite-authors [authorList]
  (if (string/blank? authorList) "" (str authorList ". "))
  )

(defn extract-year [year]
  (string/join (take 4 year))
  )

(defn journal-citation [{label :label {:keys [authorList
                                              publishedIn
                                              volume
                                              issue
                                              year
                                              startPage
                                              endPage]} :attributes}]
  (str (cite-authors authorList)
       label ". "
       publishedIn " "
       volume
       (if issue (str ", no. " issue))
       (if (or volume issue) " ")
       "(" (extract-year year) "): "
       startPage "-" endPage 
       "."
       )
  )

(defn book-citation [{label :label {:keys [authorList year publishedBy]} :attributes}]
  (str (cite-authors authorList)
       label ". "
       (if publishedBy (str publishedBy ", "))
       (extract-year year) "."
       )
  )

(defn pub-citation [{:keys [vivoType] :as json}]
  (cond
    (re-matches #".*AcademicArticle" vivoType) (journal-citation json)
    (re-matches #".*Book" vivoType) (book-citation json)
    :else (str "Cannot handle type: " vivoType)
    )
  )

