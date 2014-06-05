(ns vivo_widgets_reporter.widgets
  (:require [om.core :as om :include-macros true]
            [goog.net.Jsonp]
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

(defn set-fields [json owner]
  (let [json-in-clojure (js->clj json :keywordize-keys true)]
    (dorun (map #(om/set-state! owner % (% json-in-clojure))
                [:positions :geographicalFocus :courses :publications
                 :artisticWorks])
           )
    (set-overview (:attributes json-in-clojure) owner)
    )
  )

(defn get-jsonp [url callback]
  (.send (goog.net.Jsonp. url) "" callback))

(defn get-fields [owner]
  (get-jsonp (str base-person-url (om/get-state owner :faculty-uri))
             #(set-fields % owner))
  )

(defn get-and-set [owner url field]
  (get-jsonp (str url (params owner))
             #(om/set-state! owner field (js->clj % :keywordize-keys true)))
  )

(defn get-and-set-dated-fields [owner]
  (do
    (get-and-set owner base-publications-url :publications)
    (get-and-set owner base-art-works-url :artisticWorks)
    )
  )
