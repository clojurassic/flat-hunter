(ns flat-hunter.ingest.core
  (:require [flat-hunter.ingest.brolliet :as b]
            [flat-hunter.ingest.interface :as interface]))

(def providers
  [ :brolliet ])

(defn ingest-all
  []
  (doseq [provider providers]
    (println (interface/ingest provider))))