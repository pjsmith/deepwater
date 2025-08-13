(ns deepwater.engine.core
  (:require [deepwater.engine.game-state :as gs]
            [deepwater.engine.map-generator :as mg]
            [deepwater.engine.game-loop :as gl])
  (:gen-class))

(defn -main
  "Main function for the Deepwater Engine."
  [& args]
  (let [game-map (mg/generate-map 20 10)
        player (gs/make-player (gs/make-position 1 1) 100 10 5)
        initial-state (gs/make-game-state player game-map [] [])]
    (println "Initial Game State:")
    (prn initial-state)

    (println "\nUpdating game state (move right):")
    (let [new-state (gl/update-game-state initial-state :right)]
      (prn new-state))))
