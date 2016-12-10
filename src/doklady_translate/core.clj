(ns doklady_translate.core 
    "#MAIN" (:require[clojure.java.io :as io]
                     [clojure.string :as s]
                     [clojure-csv.core :as csv]
                     [doklady_translate.impl.core :as impl :refer[?>>]]))
(defn doklady_map 
   ;; "Parse from row vectors and return a map"
    ([rows]
     (doklady_map {} rows))
    ([{:keys [dokladySec transform-header header structs] :or {dokladySec true} :as opts} rows ] 
     let [consume-header(not header) 
          header (if header
                     header
                     (first rows)) 
          header (cond 
                     transform-header (mapv transform-header header)
                     dokladySec (mapv keyword header)
                     :else header) 
          map-fn(if structs
                    (let [s (apply create-struct header )]
                        (partial apply struct s))
                    (partial impl/doklady_map-row header))]
     
          (map map-fn
               (if consume-header
                   (rest rows)
                   rows))))

(defn cast-with
  ;;"Casts the vals of each row according to `cast-fns`, which must either be a map of
  ;;`column-name -> casting-fn` or a single casting function to be applied towards all columns.
  ;;Additionally, an `opts` map can be used to specify:
  
  ;;* `:except-first` - Leaves the first row unaltered; useful for preserving header row.
  ;;* `:exception-handler` - If cast-fn raises an exception, this function will be called with args
    ;;`colname, value`, and the result used as the parse value.
 ;; * `:only` - Only cast the specified column(s); can be either a single column name, or a vector of them."
  ([cast-fns rows]
   (cast-with cast-fns {} rows))
  ([cast-fns {:keys [except-first exception-handler only] :as opts} rows]
   (->> rows
        (?>> except-first (drop 1))
        (map #(impl/cast-row cast-fns % :only only :exception-handler exception-handler))
        (?>> except-first (cons (first rows))))))

(defn parse-and-process
  ;;"This is a convenience function for reading a csv file using `clojure/data.csv` 
   ;;`:parser-opts` can be specified and will be passed along to `clojure-csv/parse-csv`"
  [csv-readable & {:keys [parser-opts]
                   :or   {parser-opts {}}
                   :as   opts}]
  (let [rest-options (dissoc opts :parser-opts)]
    (process
      rest-options
      (impl/apply-kwargs csv/parse-csv csv-readable parser-opts))))

;;This is probably the gem of doklady, in this instance: the doklady grammar becomes doklady-csv, which is ;;pretty much a helper function that reads in data from csv.

(defn doklady-csv
  [csv-filename & {:as opts}]
  (let [rest-options (dissoc opts :parser-opts)]
    (with-open [in-file (io/reader csv-filename)]
      (doall
        (impl/apply-kwargs parse-and-process in-file opts)))))

(defn doklady_vectorize

  ([rows]
   (doklady_vectorize {} rows))
  ([{:keys [header prepend-header format-header]
     :or {prepend-header true format-header impl/stringify-keyword}}
    rows]
   ;; Grab the specified header, or the keys from the first row. We'll
   ;; use these to `get` the appropriate values for each row.
   (let [header     (or header (-> rows first keys))
         ;; This will be the formatted version we prepend if desired
         out-header (if format-header (mapv format-header header) header)]
     (->> rows
          (map
            (fn [row] (mapv (partial get row) header)))
          (?>> prepend-header (cons out-header))))))




;; "Takes sequence of items and returns a sequence of batches of items from the original
;; sequence, at most `n` long."

(defn batch
  [n rows]
  (partition n n [] rows))

(defn doklady_serpent_csv
  ;;"Convenience function for spitting out CSV data to a file using `clojure-csv`.
  ;;* `file` - Can be either a filename string, or a file handle.
  ;;* `opts` - Optional hash of settings.
  ;;* `rows` - Can be a sequence of either maps or vectors; if the former, vectorize will be
  ;; called on the input with `:header` argument specifiable through `opts`.
  ;;The Options hash can have the following mappings:
  ;;* `:batch-size` - How many rows to format and write at a time?
  ;;* `:cast-fns` - Formatter(s) to be run on row values. As with `cast-with` function, can be either a map
    ;; of `column-name -> cast-fn`, or a single function to be applied to all values. Note that `str` is called
     ;;on all values just before writing regardless of `:cast-fns`.
  ;;* `:writer-opts` - Options hash to be passed along to `clojure-csv.core/write-csv`.
  ;;* `:header` - Header to be passed along to `vectorize`, if necessary.
  ;;* `:prepend-header` - Should the header be prepended to the rows written if `vectorize` is called?"
  ([file rows]
   (doklady-serpent-csv file {} rows))
  ([file
    {:keys [batch-size cast-fns writer-opts header prepend-header]
     :or   {batch-size 20 prepend-header true}
     :as   opts}
    rows]
   (if (string? file)
     (with-open [file-handle (io/writer file)]
       (doklady-serpent-csv file-handle opts rows))
     ; Else assume we already have a file handle
     (->> rows
          (?>> cast-fns (cast-with cast-fns))
          (?>> (-> rows first map?)
               (vectorize {:header header
                           :prepend-header prepend-header}))
          ; For safe measure
          (cast-with str)
          (batch batch-size)
          (map #(impl/apply-kwargs csv/write-csv % writer-opts))
          (reduce
            (fn [w rowstr]
              (.write w rowstr)
              w)
            file)))))
