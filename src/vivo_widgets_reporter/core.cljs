(ns vivo_widgets_reporter.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [goog.net.Jsonp]
            ))

(enable-console-print!)

(def app-state (atom {
  }))

(defn base-url [type]
  (str "https://scholars-test.oit.duke.edu/widgets/api/v0.9/people/" type "/all.jsonp?uri="))

(defn create-heading [ {:keys [prefixName firstName lastName]} ]
  (str "Scholars Report for " prefixName " " firstName " " lastName)
  )

(defn create-subheading [{:keys [preferredTitle]}]
  (str preferredTitle)
  )

(defn set-overview [json owner]
  (let [json-in-clojure (first (js->clj json :keywordize-keys true))]
    (om/set-state! owner :overview (:overview json-in-clojure))
    (om/set-state! owner :heading (create-heading json-in-clojure))
    (om/set-state! owner :subheading (create-subheading json-in-clojure))
    )
  )

(defn parse-appointments [json]
  (.log js/console (str json))
  (string/join "\n\n" (map :label json))
  )

(defn set-appointments [json owner]
  (let [json-in-clojure (js->clj json :keywordize-keys true)]
    (om/set-state! owner :appointments (parse-appointments json-in-clojure))
    )
  )

(defn get-overview [owner]
  (let [url (str (base-url "overview") (om/get-state owner :faculty-uri))]
    (.send (goog.net.Jsonp. url) "" #(set-overview % owner))
    )
  )

(defn get-appointments [owner]
  (let [url (str (base-url "positions") (om/get-state owner :faculty-uri))]
    (.send (goog.net.Jsonp. url) "" #(set-appointments % owner))
    )
  )

(defn generate-report [{:keys [overview include-overview include-appointments appointments]}]
  (str
    (if include-appointments (str appointments "\n\n"))
    (if include-overview overview)
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
       :faculty-uri "https://scholars.duke.edu/individual/per2051062"

       :heading "Scholars Report"
       :subheading ""

       :include-overview true
       :include-appointments true

       :overview ""
       :appointments ""
       }
      )
    om/IWillMount
    (will-mount [this]
      (get-overview owner)
      (get-appointments owner)
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
