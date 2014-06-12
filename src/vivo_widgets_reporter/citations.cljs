(ns vivo_widgets_reporter.citations
  (:require [clojure.string :as string]
            [om.dom :as dom :include-macros true]
            [vivo_widgets_reporter.dom-utils :as dom-utils]
            )
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

(defn title [label & {:keys [surround]}]
  (str
    surround
    (if (re-matches #".*\." label) label (str label "."))
    surround
    " "
    )
  )

(defn page-numbers [start end & {:keys [prefix suffix]}]
  (if (or start end) (str prefix start "-" end suffix))
  )

(defn parenthetical [value]
  (if value (str " (" value ")")))

(defn journal-citation [{label :label {:keys [authorList
                                              publishedIn
                                              publishedBy
                                              volume
                                              issue
                                              year
                                              subtypes
                                              startPage
                                              endPage]} :attributes}]
  (dom/span nil (cite-authors authorList)
       (title label)
       (if publishedIn (dom/em nil publishedIn " ")
         (if publishedBy (dom/em nil publishedBy " ")))
       volume
       (if issue (str ", no. " issue))
       (if (or volume issue) " ")
       "(" (extract-year year) ")"
       (page-numbers startPage endPage :prefix ": ")
       "."
       (parenthetical subtypes)
       )
  )

(defn book-citation [{label :label {:keys [subtypes
                                           authorList
                                           year
                                           publishedBy]} :attributes}]
  (dom/span nil (cite-authors authorList)
       (dom/em nil (title label))
       (if publishedBy (str publishedBy ", "))
       (extract-year year) "."
       (parenthetical subtypes)
       )
  )

(defn section-citation [{label :label {:keys [authorList
                                              year
                                              startPage
                                              endPage
                                              subtypes
                                              parentBookTitle
                                              publishedBy]} :attributes}]
  (dom/span nil (cite-authors authorList)
       (title label :surround "\"")
       (if parentBookTitle (dom/em nil (title parentBookTitle)))
       (page-numbers startPage endPage :suffix ". ")
       (if publishedBy (str publishedBy ", "))
       (extract-year year) "."
       (parenthetical subtypes)
       )
  )

(defn pub-citation [{:keys [vivoType] :as json}]
  (cond
    (re-matches #".*AcademicArticle" vivoType) (journal-citation json)
    (re-matches #".*OtherArticle" vivoType)    (journal-citation json)
    (re-matches #".*ConferencePaper" vivoType) (journal-citation json)
    (re-matches #".*Dataset" vivoType)         (journal-citation json)
    (re-matches #".*Software" vivoType)        (journal-citation json)
    (re-matches #".*DigitalPublication" vivoType) (journal-citation json)
    ;Matches "EditedBook" too
    (re-matches #".*Book" vivoType)            (book-citation json)
    (re-matches #".*Report" vivoType)          (book-citation json)
    (re-matches #".*Thesis" vivoType)          (book-citation json)
    (re-matches #".*BookSection" vivoType)     (section-citation json)
    :else (journal-citation json)
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

(defn type->header [vivoType items]
  (cond
    (re-matches #".*AcademicArticle" vivoType) "Academic Articles"
    (re-matches #".*OtherArticle" vivoType)    "Other Articles"
    (re-matches #".*ConferencePaper" vivoType) "Conference Papers"
    (re-matches #".*Dataset" vivoType)         "Datasets"
    (re-matches #".*Software" vivoType)        "Software"
    (re-matches #".*DigitalPublication" vivoType) "Digital Publications"
    ;Matches "EditedBook" too
    (re-matches #".*Book" vivoType)            "Books"
    (re-matches #".*Report" vivoType)          "Reports"
    (re-matches #".*Thesis" vivoType)          "Theses"
    (re-matches #".*BookSection" vivoType)     "Book Sections"

    (re-matches #".*MultipleTypes" vivoType) "Multiple Types"
    (re-matches #".*Exhibit" vivoType) "Exhibits"
    (re-matches #".*Installation" vivoType) "Installations"
    (re-matches #".*Photograph" vivoType) "Photography"
    (re-matches #".*RadioTelevisionProgram" vivoType) "Radio / Television"
    (re-matches #".*Script" vivoType) "Scripts"
    (re-matches #".*VideoRecording" vivoType) "Video"
    (re-matches #".*duke-art-extension.*" vivoType)
      (get-in (first items) [:attributes :type_description])
    :else "Other"
    )
  )

(defn citations [data cite-fn]
  (let [sorted-data (group-by :vivoType data)]
    (apply dom/div nil
           (map
             #(dom/div nil
               (dom/h3 nil (type->header (first %) (second %)))
               (dom-utils/unstyled-list (map cite-fn (second %))))
             sorted-data))
    )
  )

(defn pub-citations [pub-data]
  (citations pub-data pub-citation))

(defn art-citations [art-data]
  (citations art-data art-work-citation))

(defn grant-listing [{label :label {:keys [startDate endDate awardedBy
                                           administeredBy]} :attributes}]
  (str label
       (if awardedBy (str ", awarded by " awardedBy))
       (if administeredBy (str ", administered by " administeredBy))
       ", " (extract-year startDate) "-" (extract-year endDate)
       )
  )
