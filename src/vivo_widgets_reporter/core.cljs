(ns vivo_widgets_reporter.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [goog.net.Jsonp]
            [vivo_widgets_reporter.citations :refer [pub-citation
                                                     art-work-citation]]
            ))

(enable-console-print!)

(def app-state (atom {
  }))

(def base-url "https://scholars-test.oit.duke.edu/widgets/api/v0.9/people/")

(def base-person-url (str base-url "complete/all.jsonp?uri="))

(def base-publications-url (str base-url "publications/all.jsonp?uri="))

(def base-artistic-works-url (str base-url "artistic_works/all.jsonp?uri="))

(defn create-heading [ {:keys [prefixName firstName lastName]} ]
  (str "Scholars Report for " prefixName " " firstName " " lastName)
  )

(defn create-subheading [{:keys [preferredTitle]}]
  (str preferredTitle)
  )

(defn parse-labels [json]
  (string/join "\n" (map :label json))
  )

(defn parse-publications [json]
  (string/join "\n\n" (map #(pub-citation %) json))
  )

(defn parse-art-works [json]
  (string/join "\n\n" (map #(art-work-citation %) json))
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

(defn get-jsonp [url callback]
  (.send (goog.net.Jsonp. url) "" callback))


(defn set-publications [json owner]
  (om/set-state! owner :publications (str "Publications\n\n" (parse-publications json)))
  )

(defn get-and-set-publications [owner]
  (get-jsonp (str base-publications-url (om/get-state owner :faculty-uri) "&start=" (om/get-state owner :start) "&end=" (om/get-state owner :end))
             #(set-publications (js->clj % :keywordize-keys true) owner))
  )

(defn set-art-works [json owner]
  (om/set-state! owner :art-works (str "Artistic Works\n\n" (parse-art-works json)))
  )

(defn set-fields [json owner]
  (.log js/console "called set-fields")
  (let [json-in-clojure (js->clj json :keywordize-keys true)]
    (set-overview (:attributes json-in-clojure) owner)
    (set-appointments (:positions json-in-clojure) owner)
    (set-geofoci (:geographicalFocus json-in-clojure) owner)
    (set-publications (:publications json-in-clojure) owner)
    (set-art-works (:artisticWorks json-in-clojure) owner)
    )
  )

(defn get-fields [owner]
  (get-jsonp (str base-person-url (om/get-state owner :faculty-uri))
             #(set-fields % owner))
  )

(defn generate-report [{:keys [include-overview overview 
                               include-appointments appointments
                               include-publications publications
                               include-art-works art-works
                               include-geofoci geofoci]}]
  ;(.log js/console publications)
  (str
    (if include-appointments (str appointments "\n\n"))
    (if include-overview     (str overview     "\n\n"))
    (if include-geofoci      (str geofoci      "\n\n"))
    (if include-publications (str publications "\n\n"))
    (if include-art-works    (str art-works    "\n\n"))
   )
  )

(defn update-preference [e owner preference]
  (om/set-state! owner preference (.. e -target -checked)))

(defn update-date-delimiter [e owner delimiter]
  (do (om/set-state! owner delimiter (.. e -target -value))
      (get-and-set-publications owner)
   )
  )

(defn requested-uri []
  (string/replace (str (.. js/document -location -search)) #"\?uri=" ""))

(defn include-checkbox [owner state preference label]
  (dom/label #js {:className "checkbox"}
    (dom/input
      #js {:type "checkbox" :checked (preference state)
           :onChange #(update-preference % owner preference)}
      )
    label))

(defn date-input [owner state delimiter label]
  (dom/div #js {:className "control-group"}
    (dom/label #js {:className "control-label"} label)
    (dom/div #js {:className "controls"}
      (dom/input #js {:type "text" :placeholder "YYYY-MM-DD"
                      :value (delimiter state)
                      :onChange #(update-date-delimiter % owner delimiter)
                      })
      )
    )
  )

(defn body [app owner]
  (reify
    om/IDisplayName
    (display-name [this]
      "Body")
    om/IInitState
    (init-state [_]
      {
       :faculty-uri (requested-uri)

       :heading "Scholars Report"

       :include-overview false
       :include-appointments false
       :include-geofoci false
       :include-publications true
       :include-art-works false
       }
      )
    om/IWillMount
    (will-mount [this]
      (get-fields owner)
      )
    om/IRenderState
    (render-state [this state]
      (dom/div nil
        (dom/h2 nil (:heading state) 
          (if-not (string/blank? (:start state))
                  (dom/span nil (str ", from " (:start state)) ))
          (if-not (string/blank? (:end state))
                  (dom/span nil (str ", until " (:end state)) ))
          )
        (dom/p nil (:subheading state))
        (dom/form #js {:className "form-inline"}
          (include-checkbox owner state :include-overview "Overview")
          (include-checkbox owner state :include-appointments "Appointments")
          (include-checkbox owner state :include-geofoci "Geographical Focus")
          (include-checkbox owner state :include-publications "Publications")
          (include-checkbox owner state :include-art-works "Artistic Works")
          )
        (dom/form #js {:className "form-horizontal"}
          (date-input owner state :start "Start Date")
          (date-input owner state :end "End Date")
          )
        (dom/div nil
          (dom/textarea
            #js {:value (generate-report state)
                 :rows 20 :className "span12"
                 })
          )
        )
      )
    ))

(om/root
  body
  app-state
  {:target (. js/document (getElementById "app"))})
