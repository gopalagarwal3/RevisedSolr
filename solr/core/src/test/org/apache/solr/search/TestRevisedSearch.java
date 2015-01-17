package org.apache.solr.search;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.MockTokenFilter;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.RandomIndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.queryparser.spans.SpanOnlyParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.OpenBitSet;
import org.apache.lucene.util._TestUtil;
import org.apache.solr.analysis.EntityFilter;
import org.apache.solr.analysis.EntityFilterFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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

public class TestRevisedSearch  extends LuceneTestCase {

    private static IndexReader reader;
    private static IndexSearcher searcher;
    private static Directory directory;
    private static Analyzer stopAnalyzer;
    private static Analyzer noStopAnalyzer;
    private static final String FIELD1 = "versionId";
    private static final String FIELD2 = "number";
    private static final String FIELD3 = "numberTrie";
    
    
    @BeforeClass
    public static void beforeClass() throws Exception {
    
      noStopAnalyzer = new Analyzer() {
        @Override
        public TokenStreamComponents createComponents(String fieldName,Reader input) {
          Tokenizer tokenizer = new MockTokenizer(input,MockTokenizer.WHITESPACE,
              true);
          
         EntityFilter filter= new EntityFilter(tokenizer,Pattern.compile("(.*)/([0-9]+)/([0-9]+)/([0-9]+)/(.*)?"),"N",false);
          return new TokenStreamComponents(tokenizer, filter);
        }
      };
      
      
      
      directory = newDirectory();
      RandomIndexWriter writer = new RandomIndexWriter(random(), directory,
          newIndexWriterConfig(TEST_VERSION_CURRENT, noStopAnalyzer)
          .setMaxBufferedDocs(_TestUtil.nextInt(random(), 100, 1000))
          .setMergePolicy(newLogMergePolicy()));
      String[] docs1 = new String[] {
          "101 ",
          "102",
          "103",
          "104",
          "105",
          "106"
          };
      
      String[] docs2 = new String[] {
          "N/8/58/2/19 N/12/72/2/17",
          "N/4/20/2/22 N/8/32/2/15",
          "N/12/65/2/15",
          "N/16/72/2/17",
          "N/20/80/2/22",
          "N/26/95/2/19",
          
          };
      String[] docs3 = new String[] {
          "19",
          "22",
          "15",
          "17",
          "22",
          "19"
          };
      
           
     // Adding the two fields in doc
      for (int i = 0; i < docs1.length; i++) {
        Document doc = new Document();
        doc.add(newTextField(FIELD1, docs1[i], Field.Store.YES));
        //Reader reader2 = new StringReader(docs2[i]);
        
        
        doc.add(newTextField(FIELD2, docs2[i], Field.Store.YES));
        doc.add(newTextField(FIELD3, docs3[i], Field.Store.YES));
        writer.addDocument(doc);
      }
      
      
      reader = writer.getReader();
      searcher = new IndexSearcher(reader);
      writer.close();
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
      reader.close();
      directory.close();
      reader = null;
      directory = null;
      stopAnalyzer = null;
      noStopAnalyzer = null;
    }
    
    /* Test case to check the span count working fine with number &
     * numberTrie field
     */
    
    @Test
    public void testSearch() throws Exception {
      SpanOnlyParser p = new SpanOnlyParser(TEST_VERSION_CURRENT, FIELD2, noStopAnalyzer);
      
      countSpansDocs( p, "[versionId:105]", 1,1); // Working fine 
  
      countSpansDocs( p, "[numberTrie:22]", 2,2); // Working fine 
          
    }
    
    @Test
    public void testNumberSearch() throws Exception {
      
      SpanOnlyParser p = new SpanOnlyParser(TEST_VERSION_CURRENT, FIELD2, noStopAnalyzer);
      countSpansDocs( p, "[number:19]",2,2); // Working fine 
      // countSpansDocs( p, "[number:15]",1,1);// Working fine 
      countSpansDocs( p, "[number:42]",0,0); // Working fine 
    
    }
    
    @Test
    public void testSpanRangeSearch() throws Exception {
      
      SpanOnlyParser p = new SpanOnlyParser(TEST_VERSION_CURRENT, FIELD2, noStopAnalyzer);
      countSpansDocs( p, "number:[10 TO 18]",4,4); // Working Fine 
    
    }
    @Test
    public void testSpanRangeProximitySearch() throws Exception {
      
      SpanOnlyParser p = new SpanOnlyParser(TEST_VERSION_CURRENT, FIELD2, noStopAnalyzer);
      countSpansDocs( p, "[number:22 number:[10 TO 18]]@~10,0",1,1); // Working fine just left proximity is more than right 
      countSpansDocs( p, "[number:19 number:[10 TO 18]]@~12,0",1,1); 
    
    }
   
    
    private void countSpansDocs(SpanOnlyParser p, String s, int spanCount,
        int docCount) throws Exception {
      SpanQuery q = (SpanQuery)p.parse(s);
      assertEquals("spanCount: " + s, spanCount, countSpans(q));
      assertEquals("docCount: " + s, docCount, countDocs(q));
    }

    private long countSpans(SpanQuery q) throws Exception {
      List<AtomicReaderContext> ctxs = reader.leaves();
      assert (ctxs.size() == 1);
      AtomicReaderContext ctx = ctxs.get(0);
      q = (SpanQuery) q.rewrite(ctx.reader());
      Spans spans = q.getSpans(ctx, null, new HashMap<Term, TermContext>());

      long i = 0;
      while (spans.next()) {
        i++;
      }
      return i;
    }

    private long countDocs(SpanQuery q) throws Exception {
      OpenBitSet docs = new OpenBitSet();
      List<AtomicReaderContext> ctxs = reader.leaves();
      assert (ctxs.size() == 1);
      AtomicReaderContext ctx = ctxs.get(0);
      IndexReaderContext parentCtx = reader.getContext();
      q = (SpanQuery) q.rewrite(ctx.reader());

      Set<Term> qTerms = new HashSet<Term>();
      q.extractTerms(qTerms);
      Map<Term, TermContext> termContexts = new HashMap<Term, TermContext>();

      for (Term t : qTerms) {
        TermContext c = TermContext.build(parentCtx, t);
        termContexts.put(t, c);
      }

      Spans spans = q.getSpans(ctx, null, termContexts);

      while (spans.next()) {
        docs.set(spans.doc());
      }
      long spanDocHits = docs.cardinality();
      // double check with a regular searcher
      TotalHitCountCollector coll = new TotalHitCountCollector();
      searcher.search(q, coll);
      assertEquals(coll.getTotalHits(), spanDocHits);
      return spanDocHits;

    }
    
    
  
}
