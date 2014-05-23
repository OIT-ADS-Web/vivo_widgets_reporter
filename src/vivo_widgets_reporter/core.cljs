(ns vivo_widgets_reporter.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.net.Jsonp]
            ))

(enable-console-print!)

(def app-state (atom {
  :faculty-uri "https://scholars.duke.edu/individual/per4284062"
  :include-overview true
  }))

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

(defn get-overview [owner]
  (let [url "https://scholars.duke.edu/widgets/api/v0.9/people/overview/5.jsonp?uri=https://scholars.duke.edu/individual/per4284062"]
    (.send (goog.net.Jsonp. url) "" #(set-overview % owner))
    )
  )

(defn generate-report [{:keys [overview include-overview]}]
  (if include-overview overview
    )
  )

(defn update-preference [e owner preference]
  (om/set-state! owner preference (.. e -target -checked))
  )

(defn body [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:include-overview true
       :overview ""
       :heading "Scholars Report"
       :subheading ""
       }
      )
    om/IWillMount
    (will-mount [this]
      (get-overview owner)
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
