(ns vivo_widgets_reporter.citations
  (:require [clojure.string :as string])
  )

(defn cite-authors [authorList]
  (if-not (string/blank? authorList) (str authorList ". "))
  )

(defn extract-year [date]
  (string/join (take 4 date)))

(defn extract-month [date year]
  (string/join (take 2 (string/replace date
                                       (re-pattern (str year "-"))
                                       ""))))

(defn extract-day [date month year]
  (string/join (take 2 (string/replace date
                                       (re-pattern (str year "-" month "-"))
                                       ""))))

(defn extract-month-year [date]
  (let [year (extract-year date)
        month (extract-month date year)]
    (str month "/" year)))

(defn extract-month-day-year [date]
  (let [year (extract-year date)
        month (extract-month date year)
        day (extract-day date month year)]
    (str month "/" day "/" year)
    )
  )

(defn journal-citation [{label :label {:keys [authorList
                                              publishedIn
                                              publishedBy
                                              volume
                                              issue
                                              year
                                              startPage
                                              endPage]} :attributes}]
  (str (cite-authors authorList)
       label ". "
       (if publishedIn (str publishedIn " ")
         (if publishedBy (str publishedBy " ")))
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

(defn extract-precise-date [{{:keys [date date_precision]} :attributes}]
  (cond
    (re-matches #".*yearPrecision" date_precision)         (extract-year date)
    (re-matches #".*yearMonthPrecision" date_precision)    (extract-month-year date)
    (re-matches #".*yearMonthDayPrecision" date_precision) (extract-month-day-year date)
    :else (extract-year date)
    )
  )

(defn art-work-citation [{label :label {:keys [collaborators type_description
                                               role commissioning_body]
                                        } :attributes :as json}]
  (str collaborators ". " label ". " role ". " type_description ". "
       (if-not (string/blank? commissioning_body)
               (str "Commissioned by " commissioning_body ". "))
       (extract-precise-date json) ".")
  )
