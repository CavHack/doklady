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
