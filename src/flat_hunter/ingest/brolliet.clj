(ns flat-hunter.ingest.brolliet
  (:require [net.cgrand.enlive-html :as html]))

;; scrapes broillet.ch to extract flat info

;; naive approach
;; 1. fetch first page (implies paginated view)
;; 2. extract page count from pagination widget
;; 3. use page count information to fetch the rest of the pages
;; 4. extract flat info from the whole list of pages

;; TODO: error handling, absence of pagination widget



(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))



(defn extract-count-of-pages
  "given a page from broillet.ch extract page count"
  [page]
  (let [select (html/select page [:td.pagination :li.numbers])
        string (first(map html/text select))]
    (->> string
         (re-find #"[0-9]+ / ([0-9]+)")
         second
         (Integer. ))))


(def ^:dynamic *base-url* "http://www.brolliet.ch/fr/locataires/louer-un-bien")

(def page-1 (fetch-url *base-url*))

(def count-pages (extract-count-of-pages page-1))

(def other-urls
  (map #(str *base-url* "/page=" %) (range 2 (inc count-pages))))

(def other-pages
  (map fetch-url other-urls))

(def attributes
    [[[:td.localite] :localite]
    [[:td.rue] :rue]
    [[:td.type] :type]
    [[:td.pieces] :pieces]
    [[:td.surface] :surface]
    [[:td.montant] :montant]])

(defn extract-sel [coll] (map first coll))

(defn extract-key [coll] (map second coll))

(def selectors (->> attributes extract-sel set))
(def flat-keys (->> attributes extract-key vec))

(defn extract-flats-from-page
  "given a full page from broillet.ch extract flat infos"
  [page]
  (->> selectors
       (html/select page)
       (map html/text)
       (partition 6)))


(defn make-map [coll]
  (map  #(zipmap flat-keys %)  coll))

(defn ingest
  "returns list of flat infos from broillet.ch"
  [](make-map
     (mapcat extract-flats-from-page (cons page-1 other-pages))))

