package org.apache.solr.search;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.spans.SpanQueryParser;
import org.apache.lucene.search.MultiTermQuery.RewriteMethod;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.TextField;
import org.apache.solr.schema.EntityTextField;
import org.apache.solr.search.QParser;

/**
 * Overrides features of Lucene's SpanQueryParser to enable
 * pulling the correct analyzer for each field and for 
 * handling non-analyzed fields.
 * 
 * This also allows Solr non-text fields to parse the 
 * appropriate components of the query string.
 * 
 * The process could be simpler, but this returns a null analyzer
 * if the field is not text and/or has a null analyzer.  The SpanQueryParser 
 * then calls the "handleNullAnalyzer..." functions when it gets a null analyzer.
 *
 * 
 */
public class SolrSpanQueryParser extends SpanQueryParser {

  private final IndexSchema schema;
  //parser to use for fields that have a null analyzer
  private final QParser nonTextParser;
  //  private static Logger log = LoggerFactory.getLogger(SolrCore.class);

  public SolrSpanQueryParser(Version matchVersion, String f, Analyzer a, IndexSchema schema, QParser nonTextParser) {
    super(matchVersion, f, a);
    this.schema = schema;
    this.nonTextParser = nonTextParser; 
  }


  @Override
  public Query handleNullAnalyzerRange(String fieldName, String start,
      String end, boolean startInclusive, boolean endInclusive){
    //lifted from SolrQueryParserBase
    SchemaField sf = schema.getFieldOrNull(fieldName);
    if (sf != null) {
      FieldType ft = sf.getType();
      return ft.getRangeQuery(nonTextParser, sf, start, end, startInclusive, endInclusive);
    }
    //if sf == null, back off to raw terms as ranges
    return super.handleNullAnalyzerRange(fieldName, start, end, startInclusive, endInclusive);
  }

  @Override
  public Query handleNullAnalyzer(String fieldName, String queryText){
    //lifted from SolrQueryParserBase
    SchemaField sf = schema.getFieldOrNull(fieldName);
    if (sf != null) {
      return sf.getType().getFieldQuery(nonTextParser, sf, queryText);
    }

    return new TermQuery(new Term(fieldName, queryText));
  }

  @Override
  public Query handleNullAnalyzerPrefix(String fieldName, String prefix) {
    //by the time you're here, you know that the analyzer was null and/or
    //this isn't a TextField
    SchemaField sf = schema.getFieldOrNull(fieldName);
    if (sf == null){
      return new TermQuery(new Term(fieldName, prefix));
    } 
    return sf.getType().getPrefixQuery(nonTextParser, sf, prefix);

  }

  /**
   * Returns analyzer to be used on full terms within a field.
   * 
   *  fieldName
   * @return analyzer to use on a requested field for whole terms.  Returns getAnalyzer() if 
   * field is not found in wholeTermAnalyzers.
   */
  @Override
  public Analyzer getAnalyzer(String fieldName){
    SchemaField field = schema.getFieldOrNull(fieldName);
    if (field == null){
      return null;
    }
    FieldType type = field.getType();
    if (type instanceof TextField){
      return((TextField)type).getQueryAnalyzer();
    }
    else if (type instanceof EntityTextField){
      return((EntityTextField)type).getQueryAnalyzer();
    }
    return null;
  }

  /**
   * Returns the multiterm analyzer to be used on a specific field.
   * Override to modify behavior.
   * 
   *  fieldName
   * @return analyzer to use on a requested field for multiTerm terms.  Returns getMultiTermAnalyzer()
   * if field is not found in multiTermAnalyzers
   */
  @Override
  public Analyzer getMultiTermAnalyzer(String fieldName){
    SchemaField field = schema.getFieldOrNull(fieldName);
    if (field == null){
      return null;
    }
    FieldType type = field.getType();

    if (type instanceof TextField){
      return((TextField)type).getMultiTermAnalyzer();
    }
    return null;
  }

  /**
   *  fieldName
   * @return RewriteMethod for a given field
   */
  @Override
  public RewriteMethod getMultiTermRewriteMethod(String fieldName) {
    SchemaField field = schema.getFieldOrNull(fieldName);
    if (field == null){
      return getMultiTermRewriteMethod();
    }
    FieldType type = field.getType();
    return type.getRewriteMethod(nonTextParser, field);
  }
}
