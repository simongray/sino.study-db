(ns user
  (:require [clojure.java.io :as io]
            [datascript.core :as d]
            [sino.study.db.core :as db]
            [sino.study.db.io.common :as io.common]
            [sino.study.db.io.cedict :as io.cedict]))

(println "started dev environment for sino.study-db")

(def conn
  (d/create-conn db/schema))

(defn conn-from-db!
  []
  (def conn
    (d/conn-from-db (io.common/read-db "db.edn"))))

(comment
  (io.cedict/read-file! conn (io/resource "cedict_ts.u8"))

  (d/q '[:find [?t ...]
         :where
         [?t :term/pinyin "san1"]
         [?t :term/script :simplified]]
       @conn)

  (count (d/q '[:find ?t
                :where
                [?t :term/script :simplified]]
              @conn))

  (d/pull-many
    @conn
    [:term/hanzi
     :term/script
     :term/definition]
    (d/q '[:find [?t ...]
           :where
           [?t :term/definition "to"]
           [?t :term/script :simplified]]
         @conn))

  (d/pull @conn [:term/hanzi
                 :term/script
                 :term/definition] 66))
