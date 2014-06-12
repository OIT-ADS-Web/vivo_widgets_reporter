(ns vivo_widgets_reporter.dom-utils
  (:require [om.dom :as dom :include-macros true]))

(defn unstyled-list [coll]
  (apply dom/ul #js {:className "unstyled"}
         (map (fn [item] (dom/li nil item)) coll)))

