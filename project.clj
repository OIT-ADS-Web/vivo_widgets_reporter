(defproject vivo_widgets_reporter "0.1.0-SNAPSHOT"
  :description "Reports for individual scholars, pulling data from Vivo Widgets."
  :url "https://github.com/OIT-ADS-Web/vivo_widgets_reporter"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.6.2"]]

  :plugins [[lein-cljsbuild "1.0.2"] [com.cemerick/clojurescript.test "0.3.1"]]

  :hooks [leiningen.cljsbuild]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "development"
              :source-paths ["src"]
              :compiler {
                :output-to "vivo_widgets_reporter.development.js"
                :output-dir "out/development"
                :optimizations :none
                :source-map true}}

             {:id "test"
              :source-paths ["src" "test"]
              :compiler {:pretty-print true
                         :preamble ["react/react.js"]
                         :output-to "vivo_widgets_reporter.test.js"
                         :output-dir "out/test"
                         :optimizations :whitespace
                         :externs ["react/externs/react.js"]
               }
             }

             {:id "production"
              :source-paths ["src"]
              :compiler {
                :output-to "vivo_widgets_reporter.production.js"
                :output-dir "out/production"
                :optimizations :advanced
                :pretty-print false
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js"]
               }}
             ]

    :test-commands {"unit-tests" ["phantomjs" :runner
                                  "test/helpers/es5-shim.js"
                                  "vivo_widgets_reporter.test.js"
                                  ]}
  }
)
