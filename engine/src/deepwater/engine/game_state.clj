(ns deepwater.engine.game-state)

(defprotocol IGameComponent
  "Protocol for things that can be placed on the game map."
  (get-glyph [this] "Returns the character glyph for the component.")
  (get-color [this] "Returns the color for the component."))

(defrecord Player [id x y hp inventory]
  IGameComponent
  (get-glyph [this] "@")
  (get-color [this] :white))

(defrecord Monster [id x y hp]
  IGameComponent
  (get-glyph [this] "o")
  (get-color [this] :green))

(defrecord Item [id name glyph color]
  IGameComponent
  (get-glyph [this] glyph)
  (get-color [this] color))

(defrecord Wall []
  IGameComponent
  (get-glyph [this] "#")
  (get-color [this] :gray))

(defrecord Floor []
  IGameComponent
  (get-glyph [this] ".")
  (get-color [this] :dark-gray))

(defn initial-state [map-width map-height]
  {:map (vec (replicate map-height (vec (replicate map-width (Floor.)))))
   :player (Player. (java.util.UUID/randomUUID) 0 0 100 [])
   :monsters []
   :items []})
