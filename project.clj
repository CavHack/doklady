(defproject doklady-translate "0.1.0-SNAPSHOT"
  :description "A CSV/JSON/TXT/UTF8 probabilistic parser built atop a Java stringbuilder JSON Smile semaphore which should allow for functional semantic parsing of streams with the ability to treat datum as tuples, the way SQL was intended to be. Read in CSV, analyze, and Collission Check with JSON File or SQL database. Allow for NoSQL functionality."
  :url "http://www.github.com/CavHack/doklady"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [congomongo "0.4.1"]
                 [bigml/clj-bigml "0.1.0"]
                 [incanter "1.5.4"]
                 [org.clojure/data.csv "0.1.3"]
                 [org.clojure/data.json "0.2.6"]
                 [com.fasterxml.jackson.core/jackson-core "2.7.5"]
                 [com.fasterxml.jackson.dataformat/jackson-dataformat-smile "2.7.5"]
                 [com.fasterxml.jackson.dataformat/jackson-dataformat-cbor "2.7.5"]
                 [tigris "0.1.1"]
                 [cheshire "5.6.3"]
		 [org.clojure/java.jdbc "0.5.8"]
		 [instaparse "1.4.1"]])
)

