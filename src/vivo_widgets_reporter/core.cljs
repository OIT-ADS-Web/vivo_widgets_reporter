(ns vivo_widgets_reporter.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [vivo_widgets_reporter.widgets :as widgets]
            [vivo_widgets_reporter.select :as select]
            [vivo_widgets_reporter.citations :refer [pub-citation
                                                     grant-listing
                                                     art-work-citation]]
            ))

(enable-console-print!)

(def app-state (atom {}))

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
                               include-positions positions
                               include-courses courses
                               include-grants grants
                               include-artisticWorks artisticWorks
                               include-publications publications 
                               include-geographicalFocus geographicalFocus]}]
  (dom/div nil
    (if include-positions    (list-section "Appointments" (map :label positions)))
    (if include-overview     (dangerous-html-section "Overview" overview))
    (if include-geographicalFocus      (list-section "Geographical Focus" (map :label geographicalFocus)))
    (if include-courses      (list-section "Courses" (map :label courses)))
    (if include-grants      (list-section "Grants" (map #(grant-listing %) grants)))
    (if include-artisticWorks    (list-section "Artistic Works" (map #(art-work-citation %) artisticWorks)))
    (if include-publications (list-section "Publications" (map #(pub-citation %) publications)))
   )
  )

(defn update-preference [e owner preference]
  (om/set-state! owner preference (.. e -target -checked)))

(defn is-a-valid-date [string]
  (re-matches #"\d{4}-(?:0|1)\d-(?:0|1)\d" string)
  )

(defn update-date-delimiter [e owner delimiter]
  (let [new-value (.. e -target -value)]
    (if (or (is-a-valid-date new-value) (string/blank? new-value))
      (do (om/set-state! owner delimiter new-value)
          (widgets/get-and-set-dated-fields owner)
          )
      ))
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
                      :className "datepicker"
                      :id (name delimiter) 
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
       :include-positions true
       :include-geographicalFocus true
       :include-publications true
       :include-artisticWorks true
       :include-courses true
       :include-grants true
       }
      )
    om/IWillMount
    (will-mount [this]
      (widgets/get-fields owner)
      )
    om/IDidMount
    (did-mount [this]
      (.. (js/jQuery ".datepicker")
          (datepicker #js {:autoclose true
                           :startView "decade"
                           :clearBtn true
                           :format "yyyy-mm-dd"})
          (on "changeDate" #(update-date-delimiter % owner
                                                   (keyword (.. % -target -id))))
          (on "clearDate" #(update-date-delimiter % owner
                                                   (keyword (.. % -target -id))))
          )
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
          (include-checkbox owner state :include-positions "Appointments")
          (include-checkbox owner state :include-geographicalFocus "Geographical Focus")
          (include-checkbox owner state :include-courses "Courses")
          (include-checkbox owner state :include-grants "Grants")
          (include-checkbox owner state :include-artisticWorks "Artistic Works")
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
