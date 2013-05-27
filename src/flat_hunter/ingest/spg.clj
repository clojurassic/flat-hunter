(ns flat-hunter.ingest.spg
  "Retrieves the collection of flats to rent from Société Privée de Gérance (SPG)"
  (:require [net.cgrand.enlive-html :as html]))

(def *base-url* "http://www.spg.ch/Accueil/Alouer.aspx")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn spg-flats []
  "Scrape the flats to rent"
  ;; TODO
)

(defn print-flats []
  "Print the resultset"
  ;; TODO
)
