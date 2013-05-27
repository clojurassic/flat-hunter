(ns flat-hunter.ingest.brolliet
  (:require [net.cgrand.enlive-html :as html]))

(def ^:dynamic *base-url* "http://www.brolliet.ch/fr/locataires/louer-un-bien")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn flats []
  (partition 6 (map html/text (html/select (fetch-url *base-url*) #{[:td.localite]
                                                                    [:td.rue]
                                                                    [:td.type]
                                                                    [:td.pieces]
                                                                    [:td.surface]
                                                                    [:td.montant]
                                                                    }))))
