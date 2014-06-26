(ns vivo_widgets_reporter.widgets
  (:require [om.core :as om :include-macros true]
            [clojure.string :as string]
            [goog.net.Jsonp]
            )
  )

;Defaults to scholars-test for local work in a file
(def domain
  (let [doc-domain (.. js/document -domain)]
    (if-not (or (string/blank? doc-domain) ; opening as file://
                (= "127.0.0.1" doc-domain) ; testing
                (= "localhost" doc-domain)) ; local dev server
      doc-domain
      "localhost:8080")
    )
  )

(def base-url (str "http://" domain "/api/v0.9/people/"))

(def base-person-url (str base-url "complete/all.jsonp?uri="))

(defn base-field-url [field]
  (str base-url field "/all.jsonp?uri="))

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
      (do (om/set-state! owner field data)
          (update-field-preference owner field true)
          )
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
    (get-and-set owner (base-field-url "publications") :publications)
    (get-and-set owner (base-field-url "artistic_works") :artisticWorks)
    (get-and-set owner (base-field-url "grants") :grants)
    )
  )
