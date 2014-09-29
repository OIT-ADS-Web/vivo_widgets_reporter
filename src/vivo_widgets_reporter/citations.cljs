(ns vivo_widgets_reporter.citations
  (:require [clojure.string :as string]
            [om.dom :as dom :include-macros true]
            [vivo_widgets_reporter.dom-utils :as dom-utils]
            )
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

(defn- strip-links [html should-keep-links]
  (if should-keep-links
    html
    (string/replace html #"</?a[^>]*>" "")
   )
  )

(defn- pub-citation [{vivoType :vivoType :as json} citation-format link-pref]
  (let [citation (get-in json [:attributes (keyword citation-format)])]
      (dom/span (clj->js {:dangerouslySetInnerHTML
                          {:__html (strip-links citation link-pref)}}))
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

(defn type->header [vivoType data]
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
    (get-in data [:attributes :type_description])
    :else "Other"
    )
  )

(defn unavailable? [data]
  (= (:label data) "No data available."))

(defn hash->sorted [hmap]
  (apply sorted-map (apply concat hmap)))

(defn citations [data cite-fn]
  (if (unavailable? (first data))
    "No data available."
    (let [sorted-data (hash->sorted (group-by #(type->header (:vivoType %) %)
                                              data))]
      (apply dom/div nil
             (map
               #(dom/div nil
                         (dom/h3 nil (first %))
                         (dom-utils/unstyled-list (map cite-fn (second %))))
               sorted-data))
      )))

(defn pub-citations [pub-data citation-format link-pref]
  (citations pub-data #(pub-citation % citation-format link-pref)))

(defn art-citations [art-data]
  (citations art-data art-work-citation))

(defn grant-listing [{label :label {:keys [startDate endDate awardedBy
                                           administeredBy]} :attributes :as data}]
  (if (unavailable? data)
    label
    (str label
         (if awardedBy (str ", awarded by " awardedBy))
         (if administeredBy (str ", administered by " administeredBy))
         ", " (extract-year startDate) "-" (extract-year endDate)
         )))
