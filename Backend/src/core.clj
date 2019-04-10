(ns core
  (:use [ring.adapter.jetty]
        [ring.middleware.params]
        [ring.middleware.json :refer [wrap-json-response]]
        [ring.util.response])
  (:gen-class))

(def t 0)
(def a -9.81)
(def v 500)

(defn velocity
  []
  (def v (+ v (* a (/ t 15))))
  (def t (inc t))
  v)

(defn handler [{{data "data"} :params}]
  (-> (response {:velocity (velocity)})
      (content-type "text/html")
      (header "Access-Control-Allow-Origin" "http://localhost:3000")))

(def app
  (-> (wrap-json-response handler) wrap-params))


(defn -main
  "This is the entry point"
  []
  (println "Hello Earth")
  ;; Can be stopped by running (.stop my-server)
  (def my-server (run-jetty app {:port 3000 :join? true})))

