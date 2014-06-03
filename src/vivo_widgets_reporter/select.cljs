(ns vivo_widgets_reporter.select
  (:require [om.dom :as dom :include-macros true])
  )

(defn select-all [id]
  (if (.getSelection js/window)
    (let [text-range (.createRange js/document)
          element (.getElementById js/document id)
          selection (.getSelection js/window)]
      (do
        (.selectNode text-range element)
        (.addRange selection text-range))
      )
    (let [text-range (.. js/document -body -createTextRange)
          element (.getElementById js/document id)]
      (do
        (.moveToElementText text-range element)
        (.select text-range)
        )
      )
    )
  )

(defn buttons [id]
  (dom/div #js {:className "pull-right "}
           (dom/a #js {:className "btn btn-large btn-primary"
                       :onClick #(select-all id)}
                  "Select all")))
