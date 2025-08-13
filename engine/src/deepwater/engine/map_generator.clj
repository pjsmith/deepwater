(ns deepwater.engine.map-generator
  (:require [deepwater.engine.game-state :as gs]))

(defn generate-map
  "Generates a simple rectangular map."
  [width height]
  (let [tiles (atom {})]
    (doseq [x (range width)
            y (range height)]
      (swap! tiles assoc (gs/make-position x y) (gs/make-tile :floor)))
    ;; Add walls around the perimeter
    (doseq [x (range width)]
      (swap! tiles assoc (gs/make-position x 0) (gs/make-tile :wall))
      (swap! tiles assoc (gs/make-position x (dec height)) (gs/make-tile :wall)))
    (doseq [y (range height)]
      (swap! tiles assoc (gs/make-position 0 y) (gs/make-tile :wall))
      (swap! tiles assoc (gs/make-position (dec width) y) (gs/make-tile :wall)))
    (gs/make-game-map width height @tiles)))
