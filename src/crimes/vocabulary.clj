(ns crimes.vocabulary)

(defn lines [filename]
  (clojure.string/split-lines (slurp filename)))

(def adjectives (lines "resources/adjectives.txt"))
(def verbal-nouns (lines "resources/verbal_nouns.txt"))
(def verbal-noun-phrases (lines "resources/verbal_noun_phrases.txt"))
(def verbs (lines "resources/verbs.txt"))
