(ns vivo_widgets_reporter.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [vivo_widgets_reporter.widgets :as widgets]
            [vivo_widgets_reporter.dom-utils :as dom-utils]
            [vivo_widgets_reporter.select :as select]
            [vivo_widgets_reporter.citations :refer [pub-citations
                                                     grant-listing
                                                     newsfeed-listing
                                                     award-listing
                                                     activity-list
                                                     art-citations
                                                     past-listing
                                                     academic-listing
                                                     gift-listing
                                                     art-event-listing]]))

(enable-console-print!)

(def app-state (atom {}))

(defn report-section [title content]
  (dom/div #js {:id (string/replace (string/lower-case title) #"\s" "-")}
    (dom/h2 nil title)
    content
    ))

(defn dangerous-html-section [title item]
  (report-section title
                  (dom/div (clj->js {:dangerouslySetInnerHTML {:__html item}}))
                  ))

(defn list-section [title items]
  (report-section title (dom-utils/unstyled-list items)))

(defn generate-report [{:keys [
                               include-overview overview
                               include-mentorship mentorship
                               include-positions positions
                               include-awards awards
                               include-geographicalFocus geographicalFocus
                               include-courses courses
                               include-grants grants
                               include-professionalActivities professionalActivities
                               include-academicActivities academicActivities
                               include-clinicalOverview clinicalOverview
                               include-artisticWorks artisticWorks
                               include-licenses licenses
                               include-pastAppointments pastAppointments
                               include-leadershipPositions leadershipPositions
                               include-academicPositions academicPositions
                               include-interestsOverview interestsOverview
                               include-gifts gifts
                               include-artisticEvents artisticEvents
                               include-teachingActivities teachingActivities
                               include-newsfeeds newsfeeds
                               include-publications publications
                               citation-format include-pub-links]}]
  (dom/div nil
    (if include-overview 
      (dangerous-html-section "Overview" overview))
    (if include-mentorship 
      (dangerous-html-section "Mentorship Availability" mentorship))
    (if include-positions 
      (list-section "Appointments" 
        (map :label positions)))
    (if include-awards 
      (list-section "Awards" 
        (map #(award-listing %) awards)))
    (if include-geographicalFocus
      (list-section "Global Scholarship"
        (map #(str (:label %) ", "
          (get-in % [:attributes :focusTypeLabel]))
            geographicalFocus)))
    (if include-courses 
      (list-section "Courses" 
        (map :label courses)))
    (if include-grants
      (list-section "Grants" 
        (map #(grant-listing %) grants)))
    (if include-professionalActivities
      (report-section "Professional Activities"
        (activity-list professionalActivities)))
    (if include-academicActivities 
      (dangerous-html-section "Academic & Administrative Activities" academicActivities))
    (if include-clinicalOverview 
      (dangerous-html-section "Clinical Activities" clinicalOverview))
    (if include-artisticWorks
      (report-section "Artistic Works" (art-citations artisticWorks)))
    (if include-licenses (list-section "Medical Licensure" (map :label licenses)))
    (if include-pastAppointments
      (list-section "Duke Appointment History" 
        (map #(past-listing %) pastAppointments)))
    (if include-leadershipPositions 
      (dangerous-html-section "Leadership & Clinical Positions" leadershipPositions))
    (if include-academicPositions
      (list-section "Academic Positions Outside Duke" 
        (map #(academic-listing %) academicPositions)))
    (if include-interestsOverview 
      (dangerous-html-section "Current Research Interests" interestsOverview))
    (if include-gifts
      (list-section "Fellowships, Supported Research, & Other Grants" 
        (map #(gift-listing %) gifts)))
    (if include-artisticEvents
      (list-section "Exhibitions, Screenings, & Performances" 
        (map #(art-event-listing %) artisticEvents)))
    (if include-teachingActivities 
      (dangerous-html-section "Teaching Activities" teachingActivities))
    (if include-newsfeeds
      (list-section "In the News" 
        (map #(newsfeed-listing %) newsfeeds)))
    (if include-publications
      (report-section "Publications" 
        (pub-citations publications
                       citation-format
                       include-pub-links)))
   ))

(defn update-preference [e owner preference]
  (om/set-state! owner preference (.. e -target -checked)))

(defn is-a-valid-date [string]
  (re-matches #"\d{4}-(?:0|1)\d-(?:0|1|2|3)\d" string)
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

(defn new-uri [url]
  (string/join ""["https://scholars.duke.edu/vivo_admin/people?uri=https://scholars.duke.edu/individual/" (last(string/split url #"/"))])
)

(defn include-checkbox [owner state preference label]
  (dom/label #js {:className "checkbox"}
    (dom/input
      #js {:type "checkbox" :checked (preference state)
           :id (name preference)
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

       :heading "Scholar Report"

       :include-overview true
       :include-mentorship true
       :include-positions true
       :include-awards true
       :include-geographicalFocus true
       :include-publications true
       :include-artisticWorks true
       :include-courses true
       :include-grants true
       :include-professionalActivities true
       :include-academicActivities true
       :include-clinicalOverview true
       :include-licenses true
       :include-pastAppointments true
       :include-leadershipPositions true
       :include-academicPositions true
       :include-interestsOverview true
       :include-gifts true
       :include-artisticEvents true
       :include-teachingActivities true
       :include-newsfeeds true
       :citation-format "chicagoCitation"
       :include-pub-links false
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
        (dom/ul #js {:className "span12 breadcrumb"}
          (dom/li nil
            (dom/a #js {:href (new-uri(requested-uri))}
                   "Back to Profile Manager Home")))
        (select/buttons "report")
        (dom/h2 nil (:heading state)
          (if-not (string/blank? (:start state))
                  (dom/span nil (str ", from " (:start state)) ))
          (if-not (string/blank? (:end state))
                  (dom/span nil (str ", until " (:end state)) ))
          )
        (dom/p nil (:subheading state))
        (dom/div #js {:className "span12 well" :id "options"}
          (dom/form #js {:className "form-inline"}
            (include-checkbox owner state :include-overview "Overview")
            (include-checkbox owner state :include-mentorship "Mentorship")
            (include-checkbox owner state :include-positions "Appointments")
            (include-checkbox owner state :include-awards "Awards")
            (include-checkbox owner state :include-geographicalFocus "Global Scholarship")
            (include-checkbox owner state :include-courses "Courses")
            (include-checkbox owner state :include-grants "Grants")
            (include-checkbox owner state :include-professionalActivities "Professional Activities")
            (include-checkbox owner state :include-academicActivities "Academic & Administrative Activities")
            (include-checkbox owner state :include-clinicalOverview "Clinical Activities")
            (include-checkbox owner state :include-artisticWorks "Artistic Works")
            (include-checkbox owner state :include-licenses "Medical Licensure")
            (include-checkbox owner state :include-pastAppointments "Duke Appointment History")
            (include-checkbox owner state :include-leadershipPositions "Leadership & Clinical Positions")
            (include-checkbox owner state :include-academicPositions "Academic Positions Outside Duke")
            (include-checkbox owner state :include-interestsOverview "Current Research Interests")
            (include-checkbox owner state :include-gifts "Fellowships, Supported Research, & Other Grants")
            (include-checkbox owner state :include-artisticEvents "Exhibitions, Screenings, & Performances")
            (include-checkbox owner state :include-teachingActivities "Teaching Activities")
            (include-checkbox owner state :include-newsfeeds "In the News")
            (include-checkbox owner state :include-publications "Publications")
            )
          (dom/form #js {:className "form-horizontal span6"}
            (date-input owner state :start "Start Date")
            (date-input owner state :end "End Date")
            )
          (if (:include-publications state)
            (dom/form nil
              (dom/label nil "Choose citation format:")
              (dom/select #js {:id "citation-format-preference"
                               :value (:citation-format state)
                               :onChange #(om/set-state! owner :citation-format
                                                         (.. % -target -value))}
                (dom/option #js {:value "chicagoCitation"}
                            "Chicago Manual of Style")
                (dom/option #js {:value "mlaCitation"}
                            "Modern Language Association (MLA)")
                (dom/option #js {:value "apaCitation"}
                            "American Psychological Association (APA)")
                (dom/option #js {:value "icmjeCitation"}
                            "International Committee of Medical Journal Editors (ICMJE)")
                )
              (include-checkbox owner state :include-pub-links "Include publication links")
              )
            )
          )
        (dom/div #js {:id "report" :className "span12 well"}
          (generate-report state)
          )
        )
      )
    ))

(om/root
  body
  app-state
  {:target (. js/document (getElementById "app"))})
