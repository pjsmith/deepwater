(ns user
  (:require [hashp.preload]
            [deepwater.server.core]))

(println "Loaded dev/user.clj")

(comment
  (require '[jsonista.core :as j])
  (require '[jsonista.tagged :as jt])

  (def mapper
    (j/object-mapper
     {:encode-key-fn true
      :decode-key-fn true
      :modules [(jt/module
                 {:handlers {Thing {:tag "!thing"
                                    :encode jt/encode-collection
                                    :decode (fn [x] (into {} x))}}})]}))

  
  (defrecord Thing [a b])
  (->Thing 1 2)
  (j/write-value-as-string (->Thing 1 2) mapper)
  (j/read-value "[\"!thing\",[[\"a\",1],[\"b\",2]]]" mapper))
