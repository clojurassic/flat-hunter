(ns flat-hunter.ingest.is
  (:require [hickory.core :as h]
  					[hickory.select :as s]))


(def immostreet-results-url-pattern
	"http://www.immostreet.ch/fr/AdZone/Lookup/d1a01cea-17f5-41b4-94cf-1040a857b48e/%d/MonthlyRent_ASC?resultPerPage=%d")

(defn result-pages
	"Returns the list of URIs to scrape"
	[num-pages results-per-page]
	(map #(format immostreet-results-url-pattern %1 results-per-page) (range 1 (inc num-pages)))
)

(defn create-htree [url]
	"Parse an URL and transform it to a hickory structure"
	(-> url slurp h/parse h/as-hickory))

(defn- links-to-flats* [results-htree]
  "Create a sequence of links to all flats classified ads; the links are absolute, yet internal to the website"
	(map :href
		(map :attrs
			(s/select
				(s/descendant 
					(s/class "classified-content") 
					(s/tag :h3) 
					(s/and (s/tag :a) (s/attr :href)))
					results-htree))))

(defn links-to-flats [one-result-page-url]
	"Returns a sequence of all URIs to the detail pages describing the flats"
	(let [doc   (create-htree one-result-page-url)
		    links (links-to-flats* doc)
	]
		(map #(str "http://www.immostreet.ch" %) links)
	))

(defn all-links-to-flats []
	(flatten (map links-to-flats (result-pages 21 50))))

(defn make-field
	[flat-elt]
	{ (-> flat-elt :attrs :id keyword)
		(-> (s/select (s/child (s/tag :dl) (s/tag :dd)) flat-elt) first :content first) }
)

(defn fetch-flat-details
	"Returns a map with the details of a flat from an URI of its detail page"
	[flat-page-uri]
	(let [
		doc (create-htree flat-page-uri)
		flat-elements (s/select
			(s/child
			  (s/id "Detail-MainFeatures-panel")
			  (s/tag :div)
			  (s/and
			  	(s/tag :dl)
			  	(s/or
			  		(s/id "AdMaterialImmoPropertySubTypeId")
			  		(s/id "Address1")
			  		(s/id "LocationID")
			  		(s/id "RentalPriceWithoutExtraCosts")
			  		(s/id "RentExtraCosts")
			  		(s/id "LivingArea")
			  		(s/id "NumberOfRooms")
			  	))) doc)
		  fields (reduce conj {} (map make-field flat-elements))
		]
		(zipmap [:description :adresse :lieu :loyer :charges :surface :nb-pieces]
			[
				(:AdMaterialImmoPropertySubTypeId fields)
				(:Address1 fields)
				(:LocationID fields)
				(:RentalPriceWithoutExtraCosts fields)
				(:RentExtraCosts fields)
				(:LivingArea fields)
				(:NumberOfRooms fields)
			])
		)
)

(defn flats
	"Return all flats details"
	[]
  (time
  	(doseq [ flat-page (all-links-to-flats) ]
	  	(print (fetch-flat-details)) (flush)
  )))

;; TODO: Read the details of each ad contained in 'is-results' and create a vector of result maps

;; Tried & working in the REPL:
;; :description  => (seq (apply :content (s/select (s/descendant (s/id "AdMaterialImmoPropertySubTypeId") (s/tag :dd)) (create-htree url))))
;; :adresse => (seq (apply :content (s/select (s/descendant (s/id "Address1") (s/tag :dd)) (create-htree url))))
;; :lieu => (seq (apply :content (s/select (s/descendant (s/id "LocationID") (s/tag :dd)) (create-htree url))))
;; :loyer => (seq (apply :content (s/select (s/descendant (s/id "RentalPriceWithoutExtraCosts") (s/tag :dd)) (create-htree url))))
;; :charges	=> (seq (apply :content (s/select (s/descendant (s/id "RentExtraCosts") (s/tag :dd)) (create-htree url))))
;; :surface => (seq (apply :content (s/select (s/descendant (s/id "LivingArea") (s/tag :dd)) (create-htree url))))
;; :nb-pieces => (seq (apply :content (s/select (s/descendant (s/id "NumberOfRooms") (s/tag :dd)) (create-htree url))))