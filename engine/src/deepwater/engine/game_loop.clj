(ns deepwater.engine.game-loop
  (:require [clojure.core.async :as async]
            [deepwater.engine.game-state :as gs]))

(defmulti process-command (fn [state command]
                            (:command command)))

(defn- get-monster-at [state x y]
  (first (filter (fn [monster]
                   (and (= (:x monster) x)
                        (= (:y monster) y)))
                 (:monsters state))))

(defn- get-item-at [state x y]
  (first (filter (fn [item]
                   (and (= (:x item) x)
                        (= (:y item) y)))
                 (:items state))))

(defn- attack-monster [state monster-id damage]
  (update-in state [:monsters] (fn [monsters]
                                 (map (fn [monster]
                                        (if (= (:id monster) monster-id)
                                          (update monster :hp - damage)
                                          monster))
                                      monsters))))

(defmethod process-command :move [state command]
  (let [{:keys [direction]} command]
    (let [player (:player state)
          [dx dy] (case direction
                    :north [0 -1]
                    :south [0 1]
                    :east [1 0]
                    :west [-1 0])
          new-x (+ (:x player) dx)
          new-y (+ (:y player) dy)]
      (if-let [monster (get-monster-at state new-x new-y)]
        (attack-monster state (:id monster) 10)
        (if (get-item-at state new-x new-y)
          state ; Cannot move into a space with an item
          (assoc-in state [:player] (-> player (update :x + dx) (update :y + dy))))))))

(defmethod process-command :get [state command]
  (let [player (:player state)
        item (get-item-at state (:x player) (:y player))]
    (if item
      (-> state
          (update-in [:player :inventory] conj item)
          (update :items (fn [items] (remove #(= (:id %) (:id item)) items))))
      state)))

(defmethod process-command :default [state command]
  (println "Unknown command:" command)
  state)

(defn- process-monster-turn [state monster]
  (let [[dx dy] (rand-nth [[-1 0] [1 0] [0 -1] [0 1]])
        new-x (+ (:x monster) dx)
        new-y (+ (:y monster) dy)]
    (if (and (not (get-monster-at state new-x new-y))
             (not (and (= new-x (get-in state [:player :x]))
                       (= new-y (get-in state [:player :y])))))
      (assoc-in state [:monsters] (mapv (fn [m]
                                          (if (= (:id m) (:id monster))
                                            (assoc m :x new-x :y new-y)
                                            m))
                                        (:monsters state)))
      state)))

(defn- process-monster-turns [state]
  (reduce process-monster-turn state (:monsters state)))

(defn start-game-loop! [game-atom in-chan out-chan]
  (async/go-loop []
    (when-let [command (async/<! in-chan)]
      (let [correlation-id (:correlation-id command)
            player-turn-state (swap! game-atom process-command command)
            monster-turn-state (swap! game-atom process-monster-turns)]
        (async/>! out-chan {:type :game-state-update
                             :state monster-turn-state
                             :correlation-id correlation-id}))
      (recur))))

