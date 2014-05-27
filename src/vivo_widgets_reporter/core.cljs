(ns vivo_widgets_reporter.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [goog.net.Jsonp]
            ))

(enable-console-print!)

(def app-state (atom {
  }))

(def base-url
  "https://scholars-test.oit.duke.edu/widgets/api/v0.9/people/complete/all.jsonp?uri=")

(defn create-heading [ {:keys [prefixName firstName lastName]} ]
  (str "Scholars Report for " prefixName " " firstName " " lastName)
  )

(defn create-subheading [{:keys [preferredTitle]}]
  (str preferredTitle)
  )

(defn parse-labels [json]
  (string/join "\n\n" (map :label json))
  )

(defn set-appointments [json owner]
  (om/set-state! owner :appointments (str "Appointments\n\n" (parse-labels json)))
  )

(defn set-overview [json owner]
  (om/set-state! owner :overview (str "Overview\n\n" (:overview json)))
  (om/set-state! owner :heading (create-heading json))
  (om/set-state! owner :subheading (create-subheading json))
  )

(defn set-geofoci [json owner]
  (om/set-state! owner :geofoci (str "Geographical Focus\n\n" (parse-labels json)))
  )

(defn set-fields [json owner]
  (let [json-in-clojure (js->clj json :keywordize-keys true)]
    (set-overview (:attributes json-in-clojure) owner)
    (set-appointments (:positions json-in-clojure) owner)
    (set-geofoci (:geographicalFocus json-in-clojure) owner)
    )
  )

(defn get-fields [owner]
  (let [url (str base-url (om/get-state owner :faculty-uri))]
    (.send (goog.net.Jsonp. url) "" #(set-fields % owner))
    )
  )

(defn generate-report [{:keys [include-overview overview 
                               include-appointments appointments
                               include-geofoci geofoci]}]
  (str
    (if include-appointments (str appointments "\n\n"))
    (if include-overview     (str overview     "\n\n"))
    (if include-geofoci      geofoci)
   )
  )

(defn update-preference [e owner preference]
  (om/set-state! owner preference (.. e -target -checked))
  )

(defn body [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {
       :faculty-uri "https://scholars.duke.edu/individual/per4284062"

       :heading "Scholars Report"

       :include-overview true
       :include-appointments true
       :include-geofoci true
       }
      )
    om/IWillMount
    (will-mount [this]
      (get-fields owner)
      )
    om/IRenderState
    (render-state [this state]
      (dom/div nil
        (dom/h1 nil (:heading state))
        (dom/p nil (:subheading state))
        (dom/input
          #js {:type "checkbox" :checked (:include-overview state)
               :onChange #(update-preference % owner :include-overview) }
          "Overview")
        (dom/input
          #js {:type "checkbox" :checked (:include-appointments state)
               :onChange #(update-preference % owner :include-appointments) }
          "Appointments")
        (dom/input
          #js {:type "checkbox" :checked (:include-geofoci state)
               :onChange #(update-preference % owner :include-geofoci) }
          "Geographical Focus")
        (dom/div nil
          (dom/textarea
            #js {:value (generate-report state)
                 :rows 20
                 })
          )
        )
      )
    ))

(om/root
  body
  app-state
  {:target (. js/document (getElementById "app"))})
