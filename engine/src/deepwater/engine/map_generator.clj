(ns deepwater.engine.map-generator
  (:require [deepwater.engine.game-state :as gs]))

(defn- place-walls [game-map]
  (let [width (count (first game-map))
        height (count game-map)]
    (reduce (fn [m [x y]]
              (assoc-in m [y x] (gs/->Wall)))
            game-map
            (for [x (range width) y (range height)
                  :when (or (zero? x) (zero? y) (= x (dec width)) (= y (dec height)))]
              [x y]))))

(defn generate-map [width height]
  (-> (gs/initial-state width height)
      (update :map place-walls)
      (assoc :player (gs/->Player (java.util.UUID/randomUUID) (quot width 2) (quot height 2) 100 []))
      (assoc :monsters [(gs/->Monster (java.util.UUID/randomUUID) 5 5 10)])
      (assoc :items [(gs/->Item (java.util.UUID/randomUUID) "Amulet of Yendor" "\"" :yellow)])))
