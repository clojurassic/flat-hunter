(ns flat-hunter.ingest.core
  (:require [flat-hunter.ingest.brolliet :as b]
            [flat-hunter.ingest.interface :as interface]))


(def providers
  [{:provider-name :brolliet, :url "http://www.brolliet.ch/fr/locataires/louer-un-bien"}])

(interface/ingest (first providers))