package org.apache.solr;

import org.apache.solr.request.SolrQueryRequest;
import org.junit.After;
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

public class TestSearch extends SolrTestCaseJ4 {
  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("solrconfig_gerrard_8020_collection2.xml","schema_gerrard_8020_collection2.xml");
    //initCore("solrconfig.xml","schema.xml");
  }
  
  @Override
  @After
  public void tearDown() throws Exception {
    clearIndex();
    super.tearDown();
  }
  
  
  /* This test case is to check different seraching scenarios 
   * on number & numberTrie field, using schema & solrconfig 
   * present in gerrard 
   */ 
  
  @Test
  public void testFirstSearch() throws Exception {

    
    // setup
    /*assertU( adoc("versionId","101", "number", "N/23/24/3/123" ) );
    assertU( adoc("versionId","102", "number", "N/23/24/3/105" ) );
    assertU(adoc("versionId","103", "number", "N/23/24/3/167" ) );
    assertU( adoc("versionId","104", "number", "N/23/24/3/103" ) );
    assertU( adoc("versionId","105", "number", "N/23/24/3/109" ));
    assertU( adoc("versionId","106", "number", "N/23/24/3/178" ) );*/
    
    // adding Number & numberTrie Field
    /*assertU( adoc("versionId", "107","number","N/21/34/3/132", "numberTrie", "132") );
    assertU( adoc("versionId", "108","number","N/24/35/3/145", "numberTrie", "145") );
    assertU( adoc("versionId", "109","number","N/28/39/3/156", "numberTrie", "156") );
    assertU( adoc("versionId", "110","number","N/32/42/3/164", "numberTrie", "164") );*/
    
    assertU( adoc("versionId", "107","number","N/21/34/3/132","money","M/5/19/3/125") );
    assertU( adoc("versionId", "108","number","N/24/35/3/145","money","M/5/19/3/130") );
    assertU( adoc("versionId", "109","number","N/28/39/3/156","money","M/5/19/3/135") );
 
    // sanity checks
    assertU(commit());
    
    /*SolrQueryRequest req = req("q", "*:*");
    assertQ(req ,"//*[@numFound='6']");*/
    
    //assertQ(req("q","numberTrie:156") ,"//*[@numFound='1']");
  //  assertQ(req("q","156") ,"//*[@numFound='1']"); // Not resulting to numfound
    //assertQ(req( "q" ,"number:164 or number:145") ,"//*[@numFound='2']");  // Working fine 
   // assertQ(req("q","*:*", "fq","number:[130 TO 140]") ,"//*[@numFound='1']"); // this is working fine now , it doing range search on numberTrie , after the change in  SolrQueryParserBase
    assertQ(req("q","*:*", "fq","numberTrie:[130 TO 150]") ,"//*[@numFound='2']"); // Working fine
  }
  
  
  @Test
  public void testMoneySearch() throws Exception {
  
  assertU( adoc("versionId", "107","number","N/21/34/3/132","money","M/5/19/3/125") );
  assertU( adoc("versionId", "108","number","N/24/35/3/145","money","M/5/19/3/130") );
  assertU( adoc("versionId", "109","number","N/28/39/3/156","money","M/5/19/3/135") );
  assertU(commit());
  
 
  assertQ(req("q","*:*", "fq","moneyTrie:[120 TO 136]") ,"//*[@numFound='2']"); // Working fine
}
}
