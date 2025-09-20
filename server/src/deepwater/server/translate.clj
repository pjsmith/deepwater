(ns deepwater.server.translate
  (:require [deepwater.engine.game-state :as s]))

(defn- translate-component [c]
  {:glyph (s/get-glyph c)
   :color (s/get-color c)})

(defn translate [game-state]
  "Translate records in a gamestate into maps for serialization"
  {:map (map #(map translate-component %) (:map game-state))
   :player (translate-component (:player game-state))
   :monsters (->> game-state :monsters (mapv translate-component))
   :items (->> game-state :items (mapv translate-component))
   })
