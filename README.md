# doklady-translate

doklady-translate is a simple example of mongoDB data query in Clojure using Incanter. 

## Usage

-git clone; cd into folder.

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
