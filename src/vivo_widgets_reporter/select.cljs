(ns vivo_widgets_reporter.select
  (:require [om.dom :as dom :include-macros true])
  )

(defn select-all [id]
  (let [element (.getElementById js/document id)]

    (if (.. js/document -body -createTextRange)
      ; IE9
      (let [text-range (.. js/document -body createTextRange)]
        (do (.moveToElementText text-range element)
            (.select text-range)))

      (let [selection (.getSelection js/window)]
        (if (.. selection -setBaseAndExtent)
          ; Chrome, Safari and Opera
          (.setBaseAndExtent selection element 0 element 1)

          ; Firefox
          (let [text-range (.createRange js/document)]
            (do (.selectNode text-range element)
                (.addRange selection text-range))
            ))))))

(defn buttons [id]
  (dom/div #js {:className "pull-right" :data-spy "affix" :data-offset-top "165"}
           (dom/a #js {:className "btn btn-large btn-primary"
                       :onClick #(select-all id)}
                  "Select Report Text")))
