Fork of apache lucene/solr 4.7.2

The aim is to run:
Span queries with precise slop by adding left slop to the query parser and allow any ordered or unordered span query.
Span queries can be mixed with all types and upto infinite levels
Pre-tagged data by NER (#Entity) can be ingested into SOLR as a xml which gives the functionality of identifying/searching for data like Money, Date, Percentage, Numbers and Ratios.
These entities can be combined in Span queries with search strings.

More to come:
Range queries on Entity current done using TermRangeQuery will be done by using Trie fields in the future. 
