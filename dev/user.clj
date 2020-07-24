(ns user
  (:require [clojure.java.io :as io]
            [datascript.core :as d]
            [sino.study.db.core :as db]
            [sino.study.db.io.common :as io.common]
            [sino.study.db.io.cedict :as io.cedict]))

(println "started dev environment for sino.study.db")

(defn look-up
  [conn definition]
  (let [db @conn]
    (d/pull-many
      db
      ['*]
      (d/q '[:find [?t ...]
             :in $ ?definition
             :where
             [?t :english/definition ?definition]
             [?t :chinese/script :simplified]]
           db
           definition))))

(comment
  ;; Load a db that has been persisted to file.
  (def conn
    (doto (d/create-conn db/schema)
      (io.cedict/read-file! (io/resource "cedict_ts.u8"))))

  ;; Create a new db from source datasets.
  (def conn
    (d/conn-from-db (io.common/read-db "db.edn")))

  ;; Entity ids for pinyin "san".
  (d/q '[:find [?t ...]
         :where
         [?t :chinese/pinyin "san"]
         [?t :chinese/script :simplified]]
       @conn)

  ;; Number of simplified entities (= dictionary entries).
  (count (d/q '[:find ?t
                :where
                [?t :chinese/script :simplified]]
              @conn))

  ;; Look-ups in English for Simplified Chinese
  (look-up conn "Hello!")
  (look-up conn "to greet")
  (look-up conn "to")
  (look-up conn "Denmark")
  (look-up conn "China")

  ;; Pull entity with :db/id 66
  (d/pull @conn ['*] 66))
