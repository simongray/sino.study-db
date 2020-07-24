(ns sino.study.db.io.cedict
  (:require [datascript.core :as d]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- line->datalog
  "Extract the constituents of a `line` in a CC-CEDICT dictionary file."
  [line]
  (let [pattern     #"^([^ ]+) ([^ ]+) \[([^]]+)\] /(.+)/"
        [_ traditional simplified pinyin+digits defs] (re-matches pattern line)
        definitions (str/split defs #"\/")
        syllables   (str/split pinyin+digits #"\s")
        pinyin      (-> pinyin+digits
                        (str/replace #"\s|\d" "")
                        (str/lower-case))]
    [{:chinese/term       traditional
      :chinese/script     :traditional
      :chinese/syllables  syllables
      :chinese/pinyin     pinyin
      :english/definition definitions}
     {:chinese/term       simplified
      :chinese/script     :simplified
      :chinese/syllables  syllables
      :chinese/pinyin     pinyin
      :english/definition definitions}]))

(defn read-file!
  "Add the listings of a CC-CEDICT dictionary `file` to a db `conn`."
  [conn file]
  (with-open [reader (io/reader file)]
    (doseq [line (line-seq reader)]
      (when-not (str/starts-with? line "#")
        (d/transact! conn (line->datalog line))))))
