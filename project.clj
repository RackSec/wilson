(defproject wilson "0.12.0-SNAPSHOT"
  :description "Opinionated Reagent bindings for Bootstrap components."
  :url "https://www.github.com/racksec/wilson"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring-server "0.4.0"]
                 [ring/ring-devel "1.4.0"]
                 [cljsjs/react "0.13.3-1"]
                 [reagent "0.5.0"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [prone "0.8.2"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [environ "1.0.0"]
                 [org.clojure/clojurescript "0.0-3308" :scope "provided"]
                 [secretary "1.2.3"]]

  :plugins [[lein-environ "1.0.0"]
            [lein-auto "0.1.2"]
            [lein-asset-minifier "0.2.2"]]

  ;; HACK: https://github.com/technomancy/leiningen/issues/1940
  :aliases {"test" ["test" "wilson.utils-test"]}

  :ring {:handler wilson.handler/app}

  :min-lein-version "2.5.0"

  :clean-targets
  ^{:protect false} [[:cljsbuild :builds :app :compiler :output-dir]
                     [:cljsbuild :builds :app :compiler :output-to]]

  :minify-assets {:assets
                  {"resources/public/css/site.min.css"
                   "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/"]
                             :compiler {:output-to "resources/public/js/app.js"
                                        :output-dir "resources/public/js/out"
                                        :asset-path "js/out"
                                        :optimizations :none
                                        :pretty-print true}}}}

  ;; :repl-options {:init-ns figwheel-sidecar.repl-api
  ;;                :init (figwheel-sidecar.repl-api/cljs-repl)}

  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]
                        [leiningen-core "2.5.1"]
                        [lein-figwheel "0.3.7"]
                        [org.clojure/tools.nrepl "0.2.10"]
                        [pjstadig/humane-test-output "0.7.0"]
                        [doo "0.1.1-SNAPSHOT"]]
         :source-paths ["env/dev/clj"]
         :plugins [[lein-figwheel "0.3.3"]
                   [lein-cljsbuild "1.0.6"]
                   [lein-doo "0.1.1-SNAPSHOT"]]
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :figwheel {:http-server-root "public"
                    :server-port 3449
                    :nrepl-port 7002
                    :css-dirs ["resources/public/css"]
                    :ring-handler wilson.handler/app}
         :env {:dev true}
         :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                    :compiler {:main "wilson.dev"
                                               :source-map true}}
                              :test {:source-paths ["src/"  "test/"]
                                     :compiler {:output-to "target/test.js"
                                                :optimizations :whitespace
                                                :pretty-print true}}}}}
   :uberjar {:aot :all}})
