(ns flat-hunter.ingest.interface)

(defmulti ingest
  "Return all flats details"
  identity)