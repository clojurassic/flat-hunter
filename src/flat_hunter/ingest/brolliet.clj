(ns flat-hunter.ingest
  (:require [net.cgrand.enlive-html :as html]))

(def *base-url* "http://www.brolliet.ch/fr/locataires/louer-un-bien")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn br-apparts []
  (partition 6 (map html/text (html/select (fetch-url *base-url*) #{[:td.localite]
                                                                    [:td.rue]
                                                                    [:td.type]
                                                                    [:td.pieces]
                                                                    [:td.surface]
                                                                    [:td.montant]
                                                                    }))))
