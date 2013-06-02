(ns flat-hunter.ingest.hg
  (:require [hickory.core :as h]
  					[hickory.select :as s]))


(def url "http://www.homegate.ch/louer/appartement-et-maison/canton-geneve/liste-annonces?mn=ctn_ge&ao=&am=&an=&a=default&tab=list&incsubs=default&l=default&fromItem=ctn_ge&be=&cid=1027939&tid=2")

;; Parse an URL and transform it to a hickory structure
(defn create-htree [url]
	(-> url slurp h/parse h/as-hickory))

;; Create a list of all flats classified ads
;; HG is protected against bot access and will show an error page and ask for entry of a capcha 
(defn link-list []
	(map :href (map :attrs (s/select (s/descendant 
																									(s/class "results") 
																									(s/class "tdTitle") 
																									(s/and 	(s/tag :a)
																													(s/attr :href))) 
																		(create-htree url)))))

(def results (link-list))

;; read each flat classified ad
(defn read-articles []
	(map create-htree results))

