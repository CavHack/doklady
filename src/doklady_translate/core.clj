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

(defn vectorize

  ([rows]
   (vectorize {} rows))
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


;; Let's see this in action:
;;
;;     => (let [data [{:this "a" :that "b"}
;;                    {:this "x" :that "y"}]]
;;          (vectorize data))
;;     (["this" "that"]
;;      ["a" "b"]
;;      ["x" "y"])
;;
;; With some options:
;;
;;     => (let [data [{:this "a" :that "b"}
;;                    {:this "x" :that "y"}]]
;;          (vectorize {:header [:that :this]
;;                      :prepend-header false}
;;                     data))
;;     (["b" "a"]
;;      ["y" "x"])


;; <br/>
;; ## batch