(ns deepwater.engine.game-state
  (:require [clojure.spec.alpha :as s]))

;; Define specs for core game entities

(s/def ::x int?)
(s/def ::y int?)
(s/def ::position (s/keys :req-un [::x ::y]))

(s/def ::health int?)
(s/def ::attack int?)
(s/def ::defense int?)

(s/def ::player (s/keys :req-un [::position ::health ::attack ::defense]))

(s/def ::monster-type keyword?)
(s/def ::monster (s/keys :req-un [::position ::health ::attack ::defense ::monster-type]))

(s/def ::item-type keyword?)
(s/def ::item (s/keys :req-un [::position ::item-type]))

(s/def ::tile-type keyword?)
(s/def ::tile (s/keys :req-un [::tile-type]))

(s/def ::width int?)
(s/def ::height int?)
(s/def ::tiles (s/map-of ::position ::tile))
(s/def ::game-map (s/keys :req-un [::width ::height ::tiles]))

(s/def ::game-state (s/keys :req-un [::player ::game-map (::monsters (s/coll-of ::monster)) (::items (s/coll-of ::item))]))

(defn make-position [x y]
  {:x x :y y})

(defn make-tile [tile-type]
  {:tile-type tile-type})

(defn make-player [position health attack defense]
  {:position position :health health :attack attack :defense defense})

(defn make-monster [position health attack defense monster-type]
  {:position position :health health :attack attack :defense defense :monster-type monster-type})

(defn make-item [position item-type]
  {:position position :item-type item-type})

(defn make-game-map [width height tiles]
  {:width width :height height :tiles tiles})

(defn make-game-state [player game-map monsters items]
  {:player player :game-map game-map :monsters monsters :items items})
