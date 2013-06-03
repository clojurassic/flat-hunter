(ns flat-hunter.util
  (:require [clj-http.client :as client]))

(def chrome-ua
  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1468.0 Safari/537.36")

(defn request
  [request-map]
  (client/request (into {:method :get
                         :headers {"User-Agent" chrome-ua}}
                        request-map)))
