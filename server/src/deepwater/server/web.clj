(ns deepwater.server.web
  (:require [reitit.ring :as reitit-ring]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [clojure.core.async :as async]))

(defn- command-handler [game]
  (fn [req]
    (let [command (get-in req [:body-params])
          correlation-id (java.util.UUID/randomUUID)
          in-chan (-> game :in-chan)
          out-chan (-> game :out-chan)]
      (async/>!! in-chan (assoc command :correlation-id correlation-id))
      (let [response (async/<!! out-chan)]
        {:status 200
         :body (:state response)}))))

(defn- state-handler [game]
  (fn [_]
    {:status 200
     :body @(:state game)}))

(defn app [game]
  (reitit-ring/ring-handler
   (reitit-ring/router
    ["/api/game"
     ["/command" {:post {:handler (command-handler game)}}]
     ["/state" {:get {:handler (state-handler game)}}]])
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/"})
    (reitit-ring/create-default-handler))
   {:middleware [parameters/parameters-middleware
                 muuntaja/format-negotiate-middleware
                 muuntaja/format-response-middleware
                 muuntaja/format-request-middleware]}))
