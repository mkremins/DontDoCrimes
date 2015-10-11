(ns crimes.bot
  (:require [crimes.generate :as gen]
            [cronj.core :as cronj :refer [cronj]]
            [twitter.api.restful :refer [statuses-update]]
            [twitter.oauth :as oauth]
            [twitter-streaming-client.core :as tsc])
  (:gen-class))

(def creds
  (oauth/make-oauth-creds
    (System/getenv "CONSUMER_KEY")
    (System/getenv "CONSUMER_SECRET")
    (System/getenv "ACCESS_TOKEN")
    (System/getenv "ACCESS_TOKEN_SECRET")))

(def mentions-stream
  (tsc/create-twitter-stream twitter.api.streaming/user-stream
    :oauth-creds creds :params {:with "user"}))

(defn tweet [_ _]
  (let [status (gen/tweet-safe gen/status)]
    (println (str "Tweeting: " status))
    (statuses-update :oauth-creds creds :params {:status status})))

(defn author [tweet]
  (-> tweet :user :screen_name))

(defn reply [mention]
  (let [username (author mention)
        status (gen/tweet-safe #(gen/reply username))]
    (println (str "Replying: " status))
    (statuses-update :oauth-creds creds
                     :params {:status status
                              :in-reply-to-status-id (:id_str mention)})))

(defn reply-to-mentions [_ _]
  (->> (:tweet (tsc/retrieve-queues mentions-stream))
       (remove #(= (author %) (System/getenv "TWITTER_USERNAME")))
       (map reply)
       dorun))

(def scheduler
  (cronj :entries [{:id "tweet-task"
                    :handler tweet
                    :schedule "0 0 /3 * * * *"}
                   {:id "reply-to-mentions-task"
                    :handler reply-to-mentions
                    :schedule "0 /5 * * * * *"}]))

(defn -main []
  (println "Starting up...")
  (cronj/start! scheduler)
  (tsc/start-twitter-stream mentions-stream)
  (println "Started!"))
