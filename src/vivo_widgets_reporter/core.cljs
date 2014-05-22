(ns vivo_widgets_reporter.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            ))

(enable-console-print!)

(def app-state (atom {
  :faculty-uri "https://scholars.duke.edu/individual/per2051062"
  :include-overview true
  }))

(defn generate-report [{:keys [include-overview]}]
  (if include-overview
    "overview here"
    "overview not here"
    )
  )

(defn update-preference [e owner preference]
  (om/set-state! owner preference (.. e -target -checked))
  )

(defn body [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:include-overview true}
      )
    om/IRenderState
    (render-state [this state]
      (dom/div nil
        (dom/h1 nil "Scholars Report")
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

