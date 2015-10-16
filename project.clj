(defproject vivo_widgets_reporter "0.1.1-SNAPSHOT"
  :description "Reports for individual scholars, pulling data from Vivo Widgets."
  :url "https://github.com/OIT-ADS-Web/vivo_widgets_reporter"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "0.9.0"]]

  :plugins [[lein-cljsbuild "1.1.0"] [com.cemerick/clojurescript.test "0.3.3"]]

  :hooks [leiningen.cljsbuild]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "development"
              :source-paths ["src"]
              :compiler {
                :output-to "out/development/vivo_widgets_reporter.development.js"
                :output-dir "out/development"
                :optimizations :none
                :source-map true}}

             {:id "test"
              :source-paths ["src" "test"]
              :compiler {:pretty-print true
                         :preamble ["react/react.js"]
                         :output-to "out/test/vivo_widgets_reporter.test.js"
                         :output-dir "out/test"
                         :optimizations :whitespace
                         :externs ["react/externs/react.js"]
               }
             }

             {:id "production"
              :source-paths ["src"]
              :compiler {
                :output-to "assets/js/vivo_widgets_reporter.production.js"
                :output-dir "out/production"
                :optimizations :advanced
                :pretty-print false
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js" "externs/jquery-1.9.js"
                          "externs/bootstrap-datepicker.js"]
               }}
             ]

    :test-commands {"unit-tests" ["phantomjs" :runner
                                  "test/helpers/es5-shim.js"
                                  "vivo_widgets_reporter.test.js"
                                  ]}
  }
)
