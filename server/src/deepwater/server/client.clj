(ns deepwater.server.client
  (:require [ring.adapter.jetty :as jetty]
            [deepwater.server.web :as web]
            [deepwater.engine.core :as engine]
            [reitit.ring :as reitit-ring]
            [hawk.core :as hawk]
            [clojure.java.io :as io]
            [org.httpkit.server :as http-kit])
  (:gen-class))

(defonce server (atom nil))

(def websocket-connections (atom #{}))

(defn websocket-handler [request]
  (http-kit/with-channel request channel
    (swap! websocket-connections conj channel)
    (http-kit/on-close channel (fn [status]
                                 (swap! websocket-connections disj channel)))))

(defn websocket-handler* [req]
  (http-kit/as-channel req {:on-open
                            (fn [ch] (swap! websocket-connections conj ch))

                            :on-close
                            (fn [ch status]
                              (swap! websocket-connections disj ch))}))

(defn broadcast-reload []
  (doseq [channel @websocket-connections]
    (when (http-kit/open? channel)
      (http-kit/send! channel "reload"))))

(defonce file-watcher 
  (hawk/watch! [{:paths ["../client"]
                 :filter hawk/file?
                 :handler (fn [ctx e]
                            (println "Client file changed:" (:file e))
                            (broadcast-reload)
                            ctx)}]))

(defn stop-file-watcher! []
  (when file-watcher
    (hawk/stop! file-watcher)))

(defn client-web-config []
  {:extra-routes [["/ws" {:get {:handler websocket-handler}}]]
   :resource-handlers [(reitit-ring/create-file-handler {:root "../client" :path "/"})]})

(defn -main [& args]
  (let [game (engine/create-game 12 8)
        app (web/app game (client-web-config))]
    (reset! server (jetty/run-jetty app {:port 3000 :join? false}))
    (println "Server started on port 3000 with client serving and live reload")))

(comment
  (do (when @server (.stop @server))
      (-main))
  )
