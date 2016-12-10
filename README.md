# doklady-translate

doklady-translate is a simple example of mongoDB data query in Clojure using Incanter. 

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

Also....

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

     (slurp-csv "test/test.csv"
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
    leave the header unaltered in the output."

>To download all the dependencies declared in the project.clj  simply run (at root) the following deps subcommand:

lein deps 

Basic Functionality

Step 1:

First, load the basic incanter library

use '(incanter core stats charts io )

Next Load some CSV data using the incanter.io/read-dataset function, which
takes a string representing either a filename or a URL to the data.

(def data 
    (read-dataset
        "https://raw.githubusercontent.com/incanter/incanter/master/data/cars.csv"
        :header true))

the default delimiter is \, but a different one can be specified with the :delim param (e.g: \tab).
The cars csv file is a small sample data set that is included in the Incanter distribution, and therefore
could have been loaded using get-dataset

(incanter/datasets/get-dataset : cars)

we can get some information on the dataset, like the number of rows and columns using either the dim function or the nrow and ncol
functions, and we can view the column names with the col-names function

user> (dim data)
[50 2]
user> (col-names data)
["Speed" "dist"]

We will use Incanter's new with-data macro and $ column-selector function to access the datatset's columns.
Within the body of a with-data expression, columns of the bound dataset can be accessed by name or index,
using the $ function, for instance ($ :colname) or ($ 0)

For example, to prepend an integer ID column to the dataset, and then display it:

(with-data (get-dataset :cars)
    (view (conj-cols (range (nrow $data)) $data)))

the conj-cols function returns a dataset by conjoining sequences together as the columns of the dataset, or by prepending or appending columns to an existing dataset, and the related conj-rows function conjoin rows.




## License

Copyright © 2016 Karl Whitford/CavHack Venire Labs Inc Lewes, De 2016.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
