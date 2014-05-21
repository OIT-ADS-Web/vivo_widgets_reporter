(defproject vivo_widgets_reporter "0.1.0-SNAPSHOT"
  :description "Reports for individual scholars, pulling data from Vivo Widgets."
  :url "https://github.com/OIT-ADS-Web/vivo_widgets_reporter"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.6.2"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "development"
              :source-paths ["src"]
              :compiler {
                :output-to "vivo_widgets_reporter.development.js"
                :output-dir "out/development"
                :optimizations :none
                :source-map true}}
             {:id "production"
              :source-paths ["src"]
              :compiler {
                :output-to "vivo_widgets_reporter.production.js"
                :output-dir "out/production"
                :optimizations :advanced
                :pretty-print false
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js"]}}
             ]})
