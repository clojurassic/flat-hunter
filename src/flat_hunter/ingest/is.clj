(ns flat-hunter.ingest.is
  (:require [hickory.core :as h]
  					[hickory.select :as s]))


(def url "http://www.immostreet.ch/fr/AdZone/Lookup/d1a01cea-17f5-41b4-94cf-1040a857b48e/2/MonthlyRent_ASC?resultPerPage=50")

;; Parse an URL and transform it to a hickory structure
(defn create-htree [url]
	(-> url slurp h/parse h/as-hickory))

;; Create a list of links to all flats classified ads
(defn link-list []
	;;FIXME: y-a pas plus simple ?
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


;; TODO: Read the details of each ad contained in 'is-results' and create a vector of result maps

;; Tried & working in the REPL:
;; :description  => (seq (apply :content (s/select (s/descendant (s/id "AdMaterialImmoPropertySubTypeId") (s/tag :dd)) (create-htree url))))
;; :adresse => (seq (apply :content (s/select (s/descendant (s/id "Address1") (s/tag :dd)) (create-htree url))))
;; :lieu => (seq (apply :content (s/select (s/descendant (s/id "LocationID") (s/tag :dd)) (create-htree url))))
;; :loyer => (seq (apply :content (s/select (s/descendant (s/id "RentalPriceWithoutExtraCosts") (s/tag :dd)) (create-htree url))))
;; :charges	=> (seq (apply :content (s/select (s/descendant (s/id "RentExtraCosts") (s/tag :dd)) (create-htree url))))
;; :surface => (seq (apply :content (s/select (s/descendant (s/id "LivingArea") (s/tag :dd)) (create-htree url))))
;; :nb-pieces => (seq (apply :content (s/select (s/descendant (s/id "NumberOfRooms") (s/tag :dd)) (create-htree url))))