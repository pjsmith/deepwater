(ns deepwater.engine.core
  (:require [deepwater.engine.map-generator :as map-gen]
            [deepwater.engine.game-loop :as loop]
            [clojure.core.async :as async]))

(defn create-game [width height]
  (let [initial-state (map-gen/generate-map width height)
        state-atom (atom initial-state)
        in-chan (async/chan)
        out-chan (async/chan)]
    (loop/start-game-loop! state-atom in-chan out-chan)
    {:state state-atom
     :in-chan in-chan
     :out-chan out-chan}))
