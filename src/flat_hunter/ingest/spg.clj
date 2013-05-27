(ns flat-hunter.ingest.spg
  "Retrieves the collection of flats to rent from Société Privée de Gérance (SPG)"
  (:require [net.cgrand.enlive-html :as html])
  (:import java.net.URL))

(def ^:dynamic *base-url* "http://www.spg.ch/Accueil/Alouer.aspx")

(defn fetch-url [url]
  (html/html-resource (URL. url)))

(defn flats []
  "Scrape the flats to rent"
  ;; TODO: evaluate the returned code; the search results are
  ;; retrieved dynamically by a subsequent 
  (html/select (fetch-url *base-url*) [:div.seResult]))

(defn print-flats []
  "Print the resultset"
  (print (flats))
)
