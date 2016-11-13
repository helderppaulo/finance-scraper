(ns finance-scraper.core
  (require [clj-http.client :as client]
           [hickory.select :as s]
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


(defn -main
  [& args]
  (println "scraping taxa DI")
  (clojure.pprint/pprint (scrape-di))
  )

