package org.apache.solr.search;

import org.apache.solr.SolrTestCaseJ4;
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

public class TestSpanQParserPluginMoneyTrie extends SolrTestCaseJ4 {
  @BeforeClass
  public static void beforeClass() throws Exception {
    //initCore("solrconfig-basic.xml","schema-spanqpplugin.xml");
    //initCore( "solrconfig_gerrard_8020_collection2.xml","schema_gerrard_8020_collection2.xml");
    //initCore( "solrconfig_gerrard_withoutTrie.xml","schema_gerrard_withoutTrie.xml");
    initCore("solrconfig_current.xml", "schema_current.xml");
    index();
  }

  public static void index() throws Exception {
      //Query testing 
    //assertU(adoc("versionId","1234","fileDataEnglish","this is document 1 after the changes on 09/10/2014 and we are working on it from 12 days. this cost us 143rs increase in my income is 123","numericEntity","N/3/17/1/1 D/5/22/10/2014-10-09 N/8/31/2/12 M/5/20/3/180"));
    assertU(adoc("versionId","1235","fileDataEnglish","this is document 1 after the changes on 09/10/2014 and we are working on it from 12 days. this cost us 100.023rs increase in my income is 123","numericEntity","N/3/17/1/1 D/5/22/10/2014-10-09 N/8/31/2/12 M/5/20/3/100.023"));
   // assertU(adoc("versionId","1235","fileDataEnglish","this is document 1 after the changes on 09/10/2014 and we are working on it from 12 days. this cost us 143rs increase in my income is 123","numericEntity","N/3/17/1/1 D/5/22/10/2014-10-09 N/8/31/2/12 M/5/20/3/196 M/6/28/3/123 M/2/28/3/111 "));
   //assertU(adoc("versionId","1235","fileDataEnglish","this is document 1 after the changes on 09/10/2014 and we are working on it from 12 days to 18 days. this cost us 143rs increase in my income is 123","numericEntity","N/3/17/1/1 D/5/22/10/2014-10-09 N/8/31/2/12 N/3/31/2/18"));
   // assertU(adoc("versionId","1235","fileDataEnglish","this is document 1 after the changes on 09/10/2014 and we are working on it from 12 days. this cost us days 100.023rs increase in my income is 123","numericEntity","N/3/17/1/1 D/5/22/10/2014-10-09 N/8/31/2/12 M/5/20/3/100.023"));
   //assertU(adoc("versionId","1235","fileDataEnglish","this is document 1 after working  we are working on it from 12 days. this cost us days increase in my income "));
    //assertU(adoc("versionId","1235","fileDataEnglish","this is document 1 after working are working on it days from 12 days to 18. this cost us increase in my income "));
   // assertU(adoc("versionId","1235","fileDataEnglish","this is document 1 after working are finance on it days from finance days to 18. this cost us increase in my income "));
    assertU(commit());
  }
  
    
  @Test
  public void testQueryFieldsMoney() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("q", "money:100.023"), "/response/numFound==1"); //running fine
  }
  @Test
  public void testQueryFieldsMoneyRange() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("q", "money:[70 TO 120]"), "/response/numFound==1"); //running fine
  }
  @Test
  public void testQueryFieldsMoneyRangeNegative() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("q", "money:[70 TO 98]"), "/response/numFound==0"); //running fine
  }

  @Test
  public void testQueryFieldsMoneyTrieRange() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("q", "moneyTrie:[0 TO *]"), "/response/numFound==1"); //running fine
  }
  
  @Test
  public void testQueryFields1() throws Exception {
   
   assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:from money:*]@~5,5"), "/response/numFound==1");
     
  }
  @Test
  public void testQueryMoneyExact() throws Exception {
   
   assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:from money:100.023]@~5,5"), "/response/numFound==1");
     
  }
  
  
  @Test
  public void testQueryFieldsMoneySpan() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:working money:[80 TO 133]]@~8,8"), "/response/numFound==1"); //running fine
  }
  
  @Test
  public void testQueryFieldsMoneySpanNegative() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:working money:[80 TO 95]]@~8,8"), "/response/numFound==0"); //running fine
  }
  @Test
  public void testQueryFieldsMoneySpanOR() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:working (fileDataEnglish:from OR money:[80 TO 112])]@~8,8"), "/response/numFound==1"); //running fine
  }
  
  @Test
  public void testQueryFieldsMoneySpanAND() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:working (fileDataEnglish:from AND money:[80 TO 112])]@~2,2"), "/response/numFound==1"); //running fine
  }
  @Test
  public void testQueryFieldsMoneySpanANDNegative() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:working (fileDataEnglish:days AND money:[80 TO 112])]@~2,2"), "/response/numFound==0"); //running fine
  }
}
