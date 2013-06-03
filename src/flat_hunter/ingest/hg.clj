(ns flat-hunter.ingest.hg
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as string]
            [flat-hunter.util :as util]))

(def ^:dynamic *base-url-fmt* "http://www.homegate.ch/louer/appartement-et-maison/canton-geneve/liste-annonces?mn=ctn_ge&ao=&am=&an=&a=default&tab=list&incsubs=default&l=default&fromItem=ctn_ge&be=&cid=1027939&tid=2&ep=%d")

(defn keep-str
  [enlive-map]
  (->> (flatten enlive-map)
       (filter string?)
       (map string/trim)
       (clojure.string/join ", ")))

(defn make-flat
  [[desc address info type+year+price & _]]
  (let [[type year price] (string/split type+year+price #", ")
        [rooms floor surface] (string/split info #", ")]
    (try
      {:desc desc
       :address address
       :price (when (seq price)
                (Integer/parseInt (string/replace price #"'|\.|-" "")))
       :type type
       :year (when (seq year)
               (Integer/parseInt year))
       :surface (when surface
                  (Integer/parseInt (string/replace surface " m2" "")))
       :rooms (Float/parseFloat (string/replace rooms " Pièces" ""))

       :floor (try (Integer/parseInt (string/replace floor ". Etage" ""))
                   (catch Exception _
                     (println :floor floor)
                     floor))}
      (catch Exception e
        (println :malformed-flat desc)))))

(def selectors #{[:td.tdTitle [:a html/first-of-type]]
                 [:td.tdStreet [:a html/first-of-type]]
                 [[:td (html/nth-of-type 5)] [:a html/first-of-type]]
                 [[:td (html/nth-of-type 6)] [:a html/first-of-type]]})

(defn flats [page]
  (println (format *base-url-fmt* (or page 1)))
  (->> (util/request {:url (format *base-url-fmt* (or page 1))})
       :body
       (html/html-snippet)
       (#(html/select % selectors))
       (map (comp keep-str :content))
       (partition (count selectors))
       (map make-flat)))

(defn fetch-max
  []
  (->> (util/request {:url (format *base-url-fmt* 1)})
       :body
       (html/html-snippet)
       (#(html/select % [:#pictureNavigation [:strong html/last-of-type]]))
       first
       :content
       first
       string/trim
       Integer/parseInt))

(defn fetch-pages
  []
  (remove nil? (mapcat flats (range 1 (inc (fetch-max))))))

;; (do
;;   (println "----------------")
;;   (clojure.pprint/pprint (flats 32)))

;; (do
;;   (println "----------------")
;;   (println :count (count (fetch-pages))))
