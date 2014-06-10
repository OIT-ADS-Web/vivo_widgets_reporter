(ns vivo_widgets_reporter.widgets
  (:require [om.core :as om :include-macros true]
            [clojure.string :as string]
            [goog.net.Jsonp]
            )
  )

;Defaults to scholars-test for local work in a file
(def domain
  (let [doc-domain (.. js/document -domain)]
    (if-not (or (string/blank? doc-domain)
                (= "localhost" doc-domain))
      doc-domain
      "scholars-test.oit.duke.edu")
    )
  )

(def base-url (str "https://" domain "/widgets/api/v0.9/people/"))

(def base-person-url (str base-url "complete/all.jsonp?uri="))

(def base-publications-url (str base-url "publications/all.jsonp?uri="))

(def base-art-works-url (str base-url "artistic_works/all.jsonp?uri="))

(defn params [owner]
  (str (om/get-state owner :faculty-uri)
       "&start=" (om/get-state owner :start)
       "&end=" (om/get-state owner :end)))

(defn create-heading [ {:keys [prefixName firstName lastName]} ]
  (str "Scholars Report for " prefixName " " firstName " " lastName)
  )

(defn create-subheading [{:keys [preferredTitle]}]
  (str preferredTitle)
  )

(defn update-field-preference [owner field value]
  (om/set-state! owner (keyword (str "include-" (name field))) value)
  )

(defn handle-new-field-data [owner field data]
  (if (= data [])
    (do (om/set-state! owner field [{:label "No data available."}])
        (update-field-preference owner field false)
        )
    (if (nil? data)
      (do (om/set-state! owner field "No data available.")
          (update-field-preference owner field false)
          )
      ;else
      (om/set-state! owner field data)
      )
    )
  )

(defn set-overview [json owner]
  (handle-new-field-data owner :overview (:overview json))
  (om/set-state! owner :heading (create-heading json))
  (om/set-state! owner :subheading (create-subheading json))
  )

(defn set-fields [json owner]
  (let [json-in-clojure (js->clj json :keywordize-keys true)]
    (dorun (map #(handle-new-field-data owner % (% json-in-clojure))
                [:positions :geographicalFocus :courses :grants :publications
                 :artisticWorks])
           )
    (set-overview (:attributes json-in-clojure) owner)
    )
  )

(defn get-jsonp [url callback]
  (let [jsonp (goog.net.Jsonp. url)]
    (do (.setRequestTimeout jsonp 20000))
    (.send jsonp "" callback)
    )
  )

(defn get-fields [owner]
  (get-jsonp (str base-person-url (om/get-state owner :faculty-uri))
             #(set-fields % owner))
  )

(defn get-and-set [owner url field]
  (get-jsonp (str url (params owner))
             #(handle-new-field-data owner field (js->clj % :keywordize-keys true)))
  )

(defn get-and-set-dated-fields [owner]
  (do
    (get-and-set owner base-publications-url :publications)
    (get-and-set owner base-art-works-url :artisticWorks)
    )
  )
