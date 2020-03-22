(ns user
  (:require [datascript.core :as ds]
            [clojure.java.io :as io]
            [sino.study.db.core :as db]
            [sino.study.db.load.cedict :as cedict]))

(println "started dev environment for sino.study-db")

(comment
  (def conn
    (ds/create-conn db/schema))

  (cedict/read-file! conn (io/resource "cedict_ts.u8"))

  (ds/q '[:find [?t ...]
          :where
          [?t :term/pinyin "san1"]
          [?t :term/script :simplified]]
        @conn)

  (count (ds/q '[:find ?t
                 :where
                 [?t :term/script :simplified]]
               @conn))

  (ds/pull-many
    @conn
    [:term/hanzi
     :term/script
     :term/definition]
    (ds/q '[:find [?t ...]
            :where
            [?t :term/definition "to"]
            [?t :term/script :simplified]]
          @conn))

  (ds/pull @conn [:term/hanzi
                  :term/script
                  :term/definition] 66))
