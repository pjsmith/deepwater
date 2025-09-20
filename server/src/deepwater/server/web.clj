(ns deepwater.server.web
  (:require
   [clojure.core.async :as async]
   [muuntaja.core :as m]
   [reitit.ring :as reitit-ring]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]

   [deepwater.server.translate :as t]))

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
     :body (t/translate @(:state game))}))

(defn app 
  ([game] (app game {}))
  ([game {:keys [extra-routes resource-handlers]}]
   (reitit-ring/ring-handler
    (reitit-ring/router
     (concat ["/api/game"
              ["/command" {:post {:handler (command-handler game)}}]
              ["/state" {:get {:handler (state-handler game)}}]]
             extra-routes)
     {:data {:muuntaja m/instance
             :middleware [parameters/parameters-middleware
                          muuntaja/format-middleware]}})
    (apply reitit-ring/routes
           (concat resource-handlers
                   [(reitit-ring/create-default-handler)])))))

(comment
  (require 'deepwater.engine.core)
  (require '[muuntaja.parse :as m.p])
  (let [game (deepwater.engine.core/create-game 80 24)]
    ((state-handler game) 1))
  (let [m' (m/create)
        req {:request-method :get, :content-type "application/json; charset=UTF-8"}
        res {:body {:a 1}}]
    (m/format-response m' req res))

  (t/translate @(:state (deepwater.engine.core/create-game 8 4)) 
               )
  )
