(ns finance-scraper.core
  (require [clj-http.client :as client]
           [hickory.select :as s]
           [clojure.java.jdbc :as sql]
           [hickory.core :as parser]))

(defn scrape-di []
  (def raw-site (:body (client/get "https://www.cetip.com.br/")))
  (def site-htree (parser/as-hickory (parser/parse raw-site)))
  (-> (s/select (s/id :ctl00_Banner_lblTaxDI) site-htree)
      first :content first
      (clojure.string/replace #"%" "")
      (clojure.string/replace #"," ".")
      (read-string))
  )

(defn persist-di [di-index]
  (sql/insert! "postgresql://172.17.0.2:5432/financeindexes"
               :indexes 
               { :timestamp (java.sql.Timestamp. (System/currentTimeMillis)) :value di-index }))


(defn -main
  [& args]
  (println "scraping taxa DI")
  (clojure.pprint/pprint (persist-di (scrape-di)))
  )

