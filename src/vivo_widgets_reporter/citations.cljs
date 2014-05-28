(ns vivo_widgets_reporter.citations
  (:require [clojure.string :as string])
  )

(defn journal-citation [{:keys [label attributes]}]
  (str (:authorList attributes) ". "
       label ". "
       (:publishedIn attributes) " "
       ;(:volume attributes) ", no. " (:issue attributes) " "
       "(" (string/join (take 4 (:year attributes))) "): "
       (:startPage attributes) "-" (:endPage attributes)
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

