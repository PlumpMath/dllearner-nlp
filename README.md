# DL-Learner + NLP = ?

"Combination of NLP-algorithms with DL-Learner algorithms"

DL-Learner: infers owl class descriptions from given data

- positive/negative examples: given examples, search for class
    description that includes as many as possible instances

NLP: given a text, extract terms from it

- to be useful, those need to be from ontologies (or otherwise
    have an assigned/agreed upon meaning)

# Goals/Ideas

* parsing the abstracts of instances of classes and then figuring
    out which schema entities (classes & properties) appear most
    often

    - the task is to evaluate nlp tools with respect to how well
        they could be used to do this
* extracting axioms from text, by inferring schema knowledge
    from it (e.g. given "Paul is a human", infer that Paul is
    an instance of the class "human")
* extracting facts from text, similar to extracting axioms, but
    with a different grammar and patterns to look out for

# Usage

The easiest way is to run the [latest jar](https://github.com/heyLu/dllearner-nlp/releases/download/v0.1.0/ner-eval-0.1.0.jar) directly:

    # Using an example config file:
    $ java -jar ner-eval-latest.jar examples/germany10-config.edn
    ...
    # If you want to use the nerd extractor, you need to set the api key in an env variable:
    $ NERD_API_KEY=<your-api-key> java -jar ner-eval-latest.jar examples/germany10-config.edn

Otherwise, you can also run this from the repl.

    # install [Leiningen](https://github.com/technomancy/leiningen)
    $ lein repl
    > (use 'ner-tools.core)
    ...
    > (annotate-text :fox "Leipzig ist eine Stadt in Deutschland")
    [{:text "Germany", :entity "http://dbpedia.org/resource/Germany", :start 21, :end 28}
     {:text "Leipzig", :entity "http://dbpedia.org/resource/Leipzig", :start 0, :end 7}]

You can look up the documentation for `annotate-text` by evaluating
`(clojure.repl/doc annotate-text)` in the repl.

# Evaluation:

* DBpedia Spotlight
    - extracts instances from dbpedia
* FRED
    - extracts tuples representing the structure of the given text
    - doesn't work with larger examples (such as dbpedia abstracts)
    - see [leipzig-fred.ttl](./examples/leipzig-fred.ttl) for an example
* OpenCalais
    - supports [generic relation extraction](http://www.opencalais.com/documentation/opencalais-web-service-api/api-metadata-english/generic-relation-extraction),
        which extracts subject-predicate-object triples from the text.

        for example, it extracts the triple `(Leipzig, be, a trade city)`
        from the dbpedia abstract for Leipzig.

        see [leipzig-opencalais.xml](./examples/leipzig-opencalais.xml) for
        more examples. (search for `GenericRelations:` in it)
* alchemyapi
    - supports [relation extraction[(http://www.alchemyapi.com/api/relation-extraction/)
        also returning SPO triples and additional information from the text.
* FOX
    - extracts instances from dbpedia
    - often enabling one of the "Fox Light" options improves the
        results (supposedly because the different nlp algorithms
        have to agree on the extracted entities, maybe merging the
        results differently would work better, for example using the
        most specific terms found (longest))
* NERD
    - entity extraction as well
    - is able to use a number of different tools, but using the
        same output format for all of them

FRED, OpenCalais and AlchemyAPI support semantic analysis. Fred often
generates classes on its own, which requires mapping them back to
something we can use or ignoring them. OpenCalais does not generate fixed
relations, it just returns triples in SPO-form. None of the tools use
relations from ontologies, they only extract verbs from the text which
would have to be mapped to relations later on.

Even though NERD only does entity extraction, it might still be useful
because it allows us to use a number of different apis.

# Resources

- background:
    * RDF/RDFS/OWL ("Semantic Web", Hitzler et. al.)
    * description logics ("The Description Logic Handbook")
    * NLP (Information Extraction, Knowledge Extraction,
        Named Entity Recognition)
    * [DL-Learner](http://dllearner.org) ([manual/introduction](http://dl-learner.org/files/dl-learner-manual.pdf))
- nlp tools
    * [DBpedia Spotlight](https://github.com/dbpedia-spotlight/dbpedia-spotlight/wiki)
        (installable web service & [demo](http://spotlight.dbpedia.org/demo/),
         [demo rest endpoint](http://spotlight.sztaki.hu:2222/rest))
        - annotates text with terms from dbpedia
    * [FRED](http://wit.istc.cnr.it/stlab-tools/fred) (web service,
        [documentation](http://wit.istc.cnr.it/stlab-tools/fred/api))
        - extracts entities and relationships between them from sentences
            (it also infers some generic classes from the linguistic
             interpretation of the sentences. e.g. noun -> class)
    * [FOX](http://aksw.org/Projects/FOX.html) (library & web service)
        - generates rdf triples containing annotations of fragments
            of the input text (e.g. Named Entity Recognition with RDF output)
        - example output only links to dbpedia
    * [NERD](http://nerd.eurecom.fr/documentation) (web service)
        - extracts annotations using various nlp tools (including dbpedia spotlight,
            opencalais, alchemy, ...), but also supports a "combined" mode
    * [alchemy](http://alchemyapi.com) (web service, [demo](http://www.alchemyapi.com/products/demo/))
    * [open calais](http://opencalais.com) (web service)
