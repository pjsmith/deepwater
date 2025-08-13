(ns deepwater.server.core
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.parameters :as parameters])
  (:gen-class))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello, Deepwater Server!"})

(def app
  (ring/ring-handler
   (ring/router
    [["/" handler]])
   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (ring/create-default-handler
     {:not-found (constantly {:status 404, :body "Not found"})
      :method-not-allowed (constantly {:status 405, :body "Method not allowed"})
      :not-acceptable (constantly {:status 406, :body "Not acceptable"})}))))

(defn -main
  "Server main function."
  [& args]
  (println "Starting Deepwater Server on port 3000...")
  (jetty/run-jetty app {:port 3000 :join? false}))