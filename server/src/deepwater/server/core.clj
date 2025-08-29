(ns deepwater.server.core
  (:require [ring.adapter.jetty :as jetty]
            [deepwater.server.web :as web]
            [deepwater.engine.core :as engine])
  (:gen-class))

(defonce server (atom nil))

(defn -main [& args]
  (let [game (engine/create-game 80 24)
        app (web/app game)]
    (reset! server (jetty/run-jetty app {:port 3000 :join? false}))
    (println "Server started on port 3000")))

(comment
  (do (when @server (.stop @server))
      (-main))
  )
