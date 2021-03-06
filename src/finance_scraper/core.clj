(ns finance-scraper.core
  (require [clj-http.client :as http]
           [hickory.select :as s]
           [hickory.core :as parser]
           [immutant.scheduling :refer :all]))

(def site-di "https://www.cetip.com.br/")

(defn parse-htree [raw]
  (parser/as-hickory (parser/parse raw)))

(defn select-di [htree]
  (->> htree
       (s/select (s/id :ctl00_Banner_lblTaxDI))
       first :content first))

(defn pct-str->number [di-str]
  (-> di-str
      (clojure.string/replace #"%" "")
      (clojure.string/replace #"," ".")
      read-string))

(defn extract-di [raw-site]
  (-> raw-site
      parse-htree
      select-di
      pct-str->number))

(defn scrape-di [callback]
  (fn [] (-> (http/get site-di)
             :body
             extract-di
             callback)))

(defn -main
  [& args]
  (prn "scheduling taxa DI scrape")
  (schedule (scrape-di prn)
    (cron "0 * * * * ?")))
