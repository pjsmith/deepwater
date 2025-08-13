(ns deepwater.engine.game-loop
  (:require [deepwater.engine.game-state :as gs]))

(defn update-game-state
  "Updates the game state based on player input."
  [game-state input]
  ;; For now, just move the player based on input
  (let [player (:player game-state)
        current-pos (:position player)
        new-pos (case input
                  :up (gs/make-position (:x current-pos) (dec (:y current-pos)))
                  :down (gs/make-position (:x current-pos) (inc (:y current-pos)))
                  :left (gs/make-position (dec (:x current-pos)) (:y current-pos))
                  :right (gs/make-position (inc (:x current-pos)) (:y current-pos))
                  current-pos) ;; No change for unknown input
        new-player (assoc player :position new-pos)]
    (assoc game-state :player new-player)))
