(ns vivo_widgets_reporter.widgets
  (:require [om.core :as om :include-macros true]
            [goog.net.Jsonp]
            [vivo_widgets_reporter.citations :refer [pub-citation
                                                     art-work-citation]]
            )
  )

(def base-url "https://scholars-test.oit.duke.edu/widgets/api/v0.9/people/")

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

(defn set-overview [json owner]
  (om/set-state! owner :overview (:overview json))
  (om/set-state! owner :heading (create-heading json))
  (om/set-state! owner :subheading (create-subheading json))
  )

(defn set-appointments [json owner]
  (om/set-state! owner :appointments (map :label json))
  )

(defn set-geofoci [json owner]
  (om/set-state! owner :geofoci (map :label json))
  )

(defn set-publications [json owner]
  (om/set-state! owner :publications (map #(pub-citation %) json))
  )

(defn set-art-works [json owner]
  (om/set-state! owner :art-works (map #(art-work-citation %) json))
  )

(defn set-fields [json owner]
  (let [json-in-clojure (js->clj json :keywordize-keys true)]
    (set-overview (:attributes json-in-clojure) owner)
    (set-appointments (:positions json-in-clojure) owner)
    (set-geofoci (:geographicalFocus json-in-clojure) owner)
    (set-publications (:publications json-in-clojure) owner)
    (set-art-works (:artisticWorks json-in-clojure) owner)
    )
  )

(defn get-jsonp [url callback]
  (.send (goog.net.Jsonp. url) "" callback))

(defn get-fields [owner]
  (get-jsonp (str base-person-url (om/get-state owner :faculty-uri))
             #(set-fields % owner))
  )

(defn get-and-set [owner url callback]
  (get-jsonp (str url (params owner))
             #(callback (js->clj % :keywordize-keys true) owner))
  )

(defn get-and-set-dated-fields [owner]
  (do
    (get-and-set owner base-publications-url set-publications)
    (get-and-set owner base-art-works-url set-art-works)
    )
  )
