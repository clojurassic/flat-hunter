(defproject flat-hunter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [enlive "1.1.1"]
                 ;; let's also give Lase a chance
                 [me.raynes/laser "1.1.1"]
                 ;; and also to hickory
                 [hickory "0.4.1"]
                 [ring "0.2.5"]
                 [clj-http "0.7.1"]
                 [net.cgrand/moustache "1.0.0-SNAPSHOT"]]
  :main flat-hunter.core)
