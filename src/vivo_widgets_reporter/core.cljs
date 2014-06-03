(ns vivo_widgets_reporter.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [goog.net.Jsonp]
            [vivo_widgets_reporter.select :as select]
            [vivo_widgets_reporter.citations :refer [pub-citation
                                                     art-work-citation]]
            ))

(enable-console-print!)

(def app-state (atom {}))

(def base-url "https://scholars-test.oit.duke.edu/widgets/api/v0.9/people/")

(def base-person-url (str base-url "complete/all.jsonp?uri="))

(def base-publications-url (str base-url "publications/all.jsonp?uri="))

(def base-art-works-url (str base-url "artistic_works/all.jsonp?uri="))

(defn create-heading [ {:keys [prefixName firstName lastName]} ]
  (str "Scholars Report for " prefixName " " firstName " " lastName)
  )

(defn create-subheading [{:keys [preferredTitle]}]
  (str preferredTitle)
  )

(defn set-appointments [json owner]
  (om/set-state! owner :appointments (map :label json))
  )

(defn set-overview [json owner]
  (om/set-state! owner :overview (:overview json))
  (om/set-state! owner :heading (create-heading json))
  (om/set-state! owner :subheading (create-subheading json))
  )

(defn set-geofoci [json owner]
  (om/set-state! owner :geofoci (map :label json))
  )

(defn get-jsonp [url callback]
  (.send (goog.net.Jsonp. url) "" callback))

(defn params [owner]
  (str (om/get-state owner :faculty-uri)
       "&start=" (om/get-state owner :start)
       "&end=" (om/get-state owner :end)))

(defn set-publications [json owner]
  (om/set-state! owner :publications (map #(pub-citation %) json))
  )

(defn set-art-works [json owner]
  (om/set-state! owner :art-works (map #(art-work-citation %) json))
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

(defn set-fields [json owner]
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

(defn report-section [title content]
  (dom/div nil 
    (dom/h3 nil title)
    content
    ))

(defn dangerous-html-section [title item]
  (report-section title
                  (dom/div (clj->js {:dangerouslySetInnerHTML {:__html item}}))
                  ))

(defn list-section [title items]
  (report-section title
                  (apply dom/ul #js {:className "unstyled"}
                         (map (fn [item] (dom/li nil item)) items))))

(defn generate-report [{:keys [include-overview overview 
                               include-appointments appointments
                               include-art-works art-works
                               include-publications publications
                               include-geofoci geofoci]}]
  (dom/div nil
    (if include-appointments (list-section "Appointments" appointments))
    (if include-overview     (dangerous-html-section "Overview" overview))
    (if include-geofoci      (list-section "Geographical Focus" geofoci))
    (if include-art-works    (list-section "Artistic Works" art-works))
    (if include-publications (list-section "Publications" publications))
   )
  )

(defn update-preference [e owner preference]
  (om/set-state! owner preference (.. e -target -checked)))

(defn update-date-delimiter [e owner delimiter]
  (do (om/set-state! owner delimiter (.. e -target -value))
      (get-and-set-dated-fields owner)
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

       :include-overview true
       :include-appointments true
       :include-geofoci true
       :include-publications true
       :include-art-works true
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
          (include-checkbox owner state :include-art-works "Artistic Works")
          (include-checkbox owner state :include-publications "Publications")
          )
        (dom/form #js {:className "form-horizontal"}
          (date-input owner state :start "Start Date")
          (date-input owner state :end "End Date")
          )
        (select/buttons "report")
        (dom/div #js {:id "report" :className "well"}
          (generate-report state)
          )
        )
      )
    ))

(om/root
  body
  app-state
  {:target (. js/document (getElementById "app"))})
