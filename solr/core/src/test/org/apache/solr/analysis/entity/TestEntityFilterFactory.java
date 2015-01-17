package org.apache.solr.analysis.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.Tokenizer;
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

public class TestEntityFilterFactory extends LuceneTestCase{
  private static IndexReader reader;
  private static IndexSearcher searcher;
  private static Directory directory;
  private static Analyzer noStopAnalyzer;
  private static final String FIELD1 = "content";
  private static final String FIELD2 = "numericEntity";
  
  
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
    String docContent = "FORM 10-Q SECURITIES AND EXCHANGE COMMISSION Washington, D.C. 20549 QUARTERLY REPORT UNDER SECTION 13 OR 15(d) OF THE SECURITIES EXCHANGE ACT OF 1934";
    String entityContent= "N/2/6/2/10 N/8/57/5/20549 N/5/42/2/13 M/2/4/2/15 D/8/39/4/1934";
 
      Document doc = new Document();
      doc.add(newTextField(FIELD1, docContent, Field.Store.YES));
      doc.add(newTextField(FIELD2, entityContent, Field.Store.YES));
      writer.addDocument(doc);

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
    noStopAnalyzer = null;
  }
  
  @Test
    public void testEntity() throws Exception {
      SpanOnlyParser p = new SpanOnlyParser(TEST_VERSION_CURRENT, FIELD1, noStopAnalyzer);
      countSpansDocs(p, "[content:exchange numericEntity:#N]~10", 4, 1);
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
