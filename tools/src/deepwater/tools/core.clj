(ns deepwater.tools.core
  (:require [deepwater.engine.map-generator :as map-gen]
            [deepwater.engine.game-state :as gs]))

(defn- render-map [game-state]
  (let [{:keys [map player monsters items]} game-state]
    (doseq [y (range (count map))]
      (doseq [x (range (count (first map)))]
        (let [tile (get-in map [y x])
              item (first (filter #(= [(:x %) (:y %)] [x y]) items))
              monster (first (filter #(= [(:x %) (:y %)] [x y]) monsters))
              player-at? (= [(:x player) (:y player)] [x y])]
          (print (cond
                   player-at? (gs/get-glyph player)
                   monster (gs/get-glyph monster)
                   item (gs/get-glyph item)
                   :else (gs/get-glyph tile)))))
      (println))))

(defn -main [& args]
  (let [game-state (map-gen/generate-map 80 24)]
    (render-map game-state)))
