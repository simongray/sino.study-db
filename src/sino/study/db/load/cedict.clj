(ns sino.study.db.load.cedict
  (:require [datascript.core :as ds]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- line->term
  "Extract the constituents of a `line` in a CC-CEDICT dictionary file."
  [line]
  (let [pattern     #"^([^ ]+) ([^ ]+) \[([^]]+)\] /(.+)/"
        [_ trad simp pinyin defs] (re-matches pattern line)
        definitions (str/split defs #"\/")]
    [{:term/hanzi      trad
      :term/script     :traditional
      :term/pinyin     pinyin
      :term/definition definitions}
     {:term/hanzi      simp
      :term/script     :simplified
      :term/pinyin     pinyin
      :term/definition definitions}]))

(defn read-file!
  "Add the listings of a CC-CEDICT dictionary `file` to a db `conn`."
  [conn file]
  (with-open [reader (io/reader file)]
    (doseq [line (line-seq reader)]
      (when-not (str/starts-with? line "#")
        (ds/transact! conn (line->term line))))))
