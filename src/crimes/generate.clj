(ns crimes.generate
  (:require [crimes.vocabulary :as vocab]))

;;; helpers

(defn rand-in-range [lower upper]
  (+ (rand-int (- upper lower)) lower))

(defn weighted-choice [& args]
  (->> args
       (partition 2)
       (mapcat (fn [[weight outcome]] (repeat weight outcome)))
       rand-nth))

(defn pluralize [n word]
  (if (= n 1) word (str word "s")))

;;; crimes

(defn adjective [] (rand-nth vocab/adjectives))
(defn verbal-noun [] (rand-nth vocab/verbal-nouns))
(defn verbal-noun-phrase [] (rand-nth vocab/verbal-noun-phrases))
(defn verb [] (rand-nth vocab/verbs))

(defn maybe-degree []
  (when (> (rand) 0.75)
    (str " in the " (rand-nth ["first" "second" "third" "fourth" "fifth"]) " degree")))

(defn crime []
  (weighted-choice
    1 (verbal-noun-phrase)
    1 (str (verbal-noun) (maybe-degree))
    1 (str (adjective) " " (verbal-noun) (maybe-degree))
    1 (str (adjective) " " (rand-nth ["activity" "behavior" "conduct"]))
    1 (str "intent to " (verb))))

(defn crimes []
  (weighted-choice
    3 (crime)
    2 (str (crime) " and " (crime))
    1 (str (crime) ", " (crime) ", and " (crime))))

;;; punishments

(defn sentence-in-days []
  (str (rand-nth [5 10 15 30 45 60 90]) " days"))

(defn sentence-in-months []
  (let [n (rand-in-range 1 90)]
    (str n " " (pluralize n "month"))))

(defn sentence-in-years []
  (let [n (weighted-choice
            3 (rand-in-range 1 16)
            2 (rand-in-range 16 46)
            1 (rand-in-range 46 210))]
    (str n " " (pluralize n "year"))))

(defn probation []
  (str (weighted-choice
         3 (sentence-in-days)
         2 (sentence-in-months)
         1 (sentence-in-years))
       " probation"))

(defn and-maybe-probation []
  (when (> (rand) 0.75)
    (str " and " (probation))))

(defn jail-time []
  (str (weighted-choice
         2 (sentence-in-days)
         4 (sentence-in-months)
         2 (sentence-in-years)
         1 "life")
       " in "
       (if (> (rand) 0.25) "prison" "jail")
       (and-maybe-probation)))

(defn community-service []
  (str (sentence-in-days) " of community service" (and-maybe-probation)))

(defn sentence []
  (weighted-choice
    3 (jail-time)
    1 (community-service)
    1 (probation)))

;;; tie it all together

(defn status []
  (str "For " (crimes) ", " (sentence) "."))

(defn reply [username]
  (str "@" username " For " (crimes) ", "
       (rand-nth ["I hereby sentence you to"
                  "I sentence you to"
                  "you are hereby sentenced to"
                  "you are sentenced to"])
       " " (sentence) "."))

(defn tweet-safe
  "Invokes `gen` repeatedly until it returns a message that's short enough to
  actually tweet, then returns that message."
  [gen]
  (->> (repeatedly gen)
       (filter #(<= (count %) 140))
       first))
