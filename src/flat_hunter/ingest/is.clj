(ns flat-hunter.ingest.is
  (:require [hickory.core :as h]
  					[hickory.select :as s]))


(def url "http://www.immostreet.ch/fr/AdZone/Lookup/d1a01cea-17f5-41b4-94cf-1040a857b48e/2/MonthlyRent_ASC?resultPerPage=50")

;; Parse an URL and transform it to a hickory structure
(defn create-htree [url]
	(-> url slurp h/parse h/as-hickory))

;; Create a list of links to all flats classified ads
(defn link-list []
	(map :href (map :attrs (s/select (s/descendant 
																									(s/class "classified-content") 
																									(s/tag :h3) 
																									(s/and 	(s/tag :a)
																													(s/attr :href))) 
																		(create-htree url)))))

(def results (link-list))

(def is-results (map #(str "http://www.immostreet.ch" %) results))


;; read each flat classified ad
(defn read-articles []
	(map create-htree is-results))