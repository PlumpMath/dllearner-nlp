(ns ner-eval.core
  "Evaluate NER capabilities of nlp tools.

# steps

- retrieve entities from dbpedia (select by class)
    * using the sparql endpoint
    * which format to use? (json, csv, ...)
- get abstracts for those entities
- process abstracts using nlp tools
    * start with nerd, it supports many things (api might be cumbersome, though)
    * result are extracted entities (vector of places in the text with info)
- basic stats
    * number of extracted entities
- more reasonable stats
    * overlap
        - which tools also find this
        - do tools different tools find 'bigger' entities for the same (or similar) text?
        - (maybe there's a library for diffing in clj?)
    * which entities are mapped (different ones for the same text)
- comparison with hand-tagged data
    * how many places also matched
    * to which entities

# data model

- everything in vectors
- from sparql: just the names
- with abstracts: {:name \"...\", :abstract \"...\"} (maybe also comment?)
- entity extraction: vector of maps, name, abstract, entities:
    * entities: `[{:text \"...\", :start 0, :end 10, :entity \"<url>\"}]`"
  (:require [clj-http.client :as http]
            [clojure.string :as str]
            [clojure.edn :as edn]

            [ner-eval.nerd :as nerd]
            [ner-eval.fox :as fox]))

(defn simple-tsv [str]
  (mapv (fn [line]
          (mapv edn/read-string (str/split line #"\t")))
        (str/split str #"\n")))

(defn dbpedia-query
  "send a sparql query to the dbpedia endpoint returning a vector of result tuples."
  [sparql-str]
  (-> (http/get "http://dbpedia.org/sparql"
                {:query-params {:query sparql-str
                                :format "text/tab-separated-values"}})
      :body
      simple-tsv))

(defn query-cities [n]
  (str "
select ?city ?abstract
where {
  ?city #rdf:type dbpedia-owl:Town ;
        dbpedia-owl:country dbpedia:Germany ;
        dbpedia-owl:abstract ?abstract ;
        dbpedia-owl:populationTotal ?population .
  filter (lang(?abstract) = \"de\")
  optional { ?city dbpedia-owl:capital ?capital }
  filter (!bound(?capital))
}
order by desc(?population)
limit " n))

(defn annotate-text [extractor text & args]
  (let [ann-fn (case extractor
                 :nerd nerd/annotate-text*
                 :fox fox/annotate-text*)]
    (apply ann-fn text args)))