(ns sino.study.db.io.common
  (:require [clojure.edn :as edn]
            [datascript.core :as d]))

(defn persist-db!
  "Persist a `db` into a `file`."
  ([db file {:keys [format] :or {format :edn} :as opts}]
   (if (= format :edn)
     (spit file (pr-str db))
     (throw (ex-info (str "unsupported format: " format) opts))))
  ([db file]
   (persist-db! db file nil)))

(defn read-db
  "Read a persisted database from a `file`. Used as input to conn-from-db."
  ([file {:keys [format] :or {format :edn} :as opts}]
   (if (= format :edn)
     (edn/read-string {:readers d/data-readers} (slurp file))
     (throw (ex-info (str "unsupported format: " format) opts))))
  ([file]
   (read-db nil)))
