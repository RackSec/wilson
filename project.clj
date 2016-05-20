(defproject wilson "0.23.0"
  :description "Opinionated Reagent bindings for Bootstrap components."
  :url "https://www.github.com/racksec/wilson"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]

  :dependencies [[org.clojure/clojurescript "1.8.51" :scope "provided"]
                 [reagent "0.5.1"]
                 [hiccup "1.0.5"]
                 [secretary "1.2.3"]
                 [org.clojure/core.match "0.3.0-alpha4"]]

  :plugins [[lein-environ "1.0.2"]
            [lein-asset-minifier "0.2.8"]
            [lein-cljfmt "0.3.0"]]

  :ring {:handler wilson.handler/app}

  :min-lein-version "2.6.1"

  :clean-targets
  ^{:protect false} [[:cljsbuild :builds :app :compiler :output-dir]
                     [:cljsbuild :builds :app :compiler :output-to]]

  :minify-assets {:assets
                  {"resources/public/css/site.min.css"
                   "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/" "env/"]
                             :compiler {:output-to "resources/public/js/app.js"
                                        :output-dir "resources/public/js/out"
                                        :asset-path "js/out"
                                        :pretty-print true
                                        :source-map true
                                        :main "wilson.dev"}}
                       :test {:source-paths ["src/"  "test/"]
                              :compiler {:output-to "target/test.js"
                                         :optimizations :whitespace
                                         :pretty-print true}}}}

  :figwheel {:http-server-root "public"
             :server-port 3449
             :css-dirs ["resources/public/css"]
             :ring-handler wilson.handler/app}

  ;; (figwheel-sidecar.repl-api/start-figwheel!)
  ;; (figwheel-sidecar.repl-api/cljs-repl)

  :repl-options {:init (use 'figwheel-sidecar.repl-api)
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles
  {:dev {:dependencies [[org.clojure/clojure "1.8.0"]
                        [ring "1.4.0"]
                        [ring-server "0.4.0"]
                        [ring/ring-devel "1.4.0"]
                        [ring/ring-defaults "0.2.0"]
                        [ring-mock "0.1.5"]
                        [prone "1.1.1"]
                        [compojure "1.5.0"]

                        [environ "1.0.2"]

                        [figwheel-sidecar "0.5.2"]
                        [com.cemerick/piggieback "0.2.1"]]
         :plugins [[lein-figwheel "0.5.2"]
                   [lein-cljsbuild "1.1.3"]
                   [lein-doo "0.1.6"]]
         :env {:dev true}}
   :uberjar {:aot :all}})
