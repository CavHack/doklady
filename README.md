# doklady_translate

doklady_translate is inspired by PageRank: our end-goal is the ability to build a clojure LISP-friendly domain-ketchup: by domain-ketchup we refer to an implementation of a semantic parser, which like SQL, allows us to treat doklady_language as a domain bypass which does not lose the inherent benefits of a domain language: in fact, with doklady_translate you are encouraged to treat SQL as SQL; a mathematical trick to treat datum as tuples; tuples and monoids are categories of multiplicative artin groups(more on this later). In plain english: doklady_translate allows us to read into a CSV, TXT(UTF-8) and collision-check with a SQL database using clojure's JSON Smiles in a lazy sequence of chars. The collision check algorithm should in addition parse a result on the terminal as you would treat any repl command.

## Usage

-git clone; cd into folder.

There are a plethora of ways you can use doklady. Because it is written in pure clojure, you have the freedom to parse CSV data using our mapreduce embedded inside doklady_map. 

;; To start, require this namespace, `clojure.java.io`, and your favorite CSV parser (e.g.,
;; [clojure-csv](https://github.com/davidsantiago/clojure-csv) or 
;; [clojure/data.csv](https://github.com/clojure/data.csv); we'll mostly be using the former).

     (require '[semantic-csv.core :refer :all]
              '[clojure-csv.core :as csv]
              '[clojure.java.io :as io])

For example, remember that with the Java I/O we can implement a quick call to fetch the data written inside a text file.

(with-open [r (clojure.java.io/input-stream "myfile.txt")] 
         (loop [c (.read r)] 
           (if (not= c -1)
             (do 
               (print (char c)) 
               (recur (.read r))))))

The same procedure is similar for fetchning csv.

(defn write-csv-file
  "Writes a csv file using a key and an s-o-s (sequence of sequences)"
  [out-sos out-file]

  (spit out-file "" :append false)
  (with-open [out-data (io/writer out-file)]
      (csv/write-csv out-data out-sos)))

With doklady you could use the following:

     user=> (with-open [in-file (io/reader "db/db.csv")]
          (->>
            (csv/parse-csv in-file)
            remove-comments
            doklady_map
            doall))

You should get something like this:

     ({:header1 "CompanyPriority", :header2 "role", :header3 "customer name"}
      {:header2 "2CompanyPriority", :header2 "role", :header "customer name"})


doklady_csv is a classic parser you can use for a non-lazy traversal using cvs-filename

     (doklady-csv "db/db.csv"
     :cast-fns {:this #(Integer/parseInt %)})

Let's delve into cast-fns:

Remember that cast-fns, as established in this article, is a mapping function which encapsulates how rows and columns are defined, parsed, and interpreted by the machine; it provides a mapping function which queries first row/item onward, ideally this should be handled more efficiently, but it is a helper functiona after all.


Now, onto doklady_vector: This function Takes a sequence of maps, and transform them into a sequence of vectors. Options:

    `:header` - The header to be used. If not specified, this defaults to `(-> rows first keys)`. Only
    values corresponding to the specified header will be included in the output, and will be included in the
    order corresponding to this argument.
    `:prepend-header` - Defaults to true, and controls whether the `:header` vector should be prepended
    to the output sequence.
   `:format-header` - If specified, this function will be called on each element of the `:header` vector, and
    the result prepended to the output sequence. The default behaviour is to leave strings alone but stringify
    keyword names such that the `:` is removed from their string representation. Passing a falsey value will
    leave the header unaltered in the output.





## License

Copyright © 2016 Karl Whitford/CavHack Venire Labs Inc Lewes, De 2016.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
