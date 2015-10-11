(defproject crimes "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [im.chit/cronj "1.4.3"]
                 [twitter-api "0.7.8"]
                 [twitter-streaming-client/twitter-streaming-client "0.3.2"]]
  :min-lein-version "2.0.0"
  :main ^:skip-aot crimes.bot
  :uberjar-name "crimes-standalone.jar"
  :profiles {:uberjar {:aot :all}})
