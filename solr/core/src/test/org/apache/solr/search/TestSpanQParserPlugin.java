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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.SolrTestCaseJ4;
import org.junit.BeforeClass;
import org.junit.Test;

/** Simple tests for SpanQParserPlugin. */
//Thank you, TestSimpleQParserPlugin, for the the model for this!

public class TestSpanQParserPlugin extends SolrTestCaseJ4 {
  @BeforeClass
  public static void beforeClass() throws Exception {
    //initCore("solrconfig-basic.xml","schema-spanqpplugin.xml");
    //initCore( "solrconfig_gerrard_8020_collection2.xml","schema_gerrard_8020_collection2.xml");
   // initCore( "solrconfig_gerrard_withoutTrie.xml","schema_gerrard_withoutTrie.xml");
  initCore("solrconfig_current.xml", "schema_current.xml");
    index();
  }

  public static void index() throws Exception {
   // assertU(adoc("versionId","999","fileDataEnglish","Today is 2012-08-01","numericEntity","D/2/23/10/2012-08-01"));
   //assertU(adoc("versionId","999","fileDataEnglish","Today is facebook introduce 2012-08-01","numericEntity","D/4/29/10/2012-08-01"));
    
   // assertU(adoc("versionId","999","fileDataEnglish","Appendix 3B  New issue announcement        + See chapter 19 for defined terms.    01/08/2012  Appendix 34 is from 1234 from calculating 21/10/1989","numericEntity","N/8/58/2/19 D/4/23/10/2012-08-01 N/2/10/2/34 N/3/8/4/1234 D/3/18/10/1989-10-21"));
    
    //Query testing 
    //assertU(adoc("versionId","1234","fileDataEnglish","this is document 1 after the changes on 09/10/2014 and we are working on it from 12 days. this cost us 125rs","numericEntity","N/3/17/1/1 D/5/20/10/2014-10-09 N/8/31/2/12 M/5/19/3/125"));
    assertU(adoc("versionId","1250","fileDataEnglish","Profit rates decrease from 12 to low value 18 on 21/07/2014 ","numericEntity","N/4/28/2/12 N/4/44/2/18 D/2/50/10/2014-07-21"));
   // assertU(adoc("versionId","1251","fileDataEnglish","Profit rates decrease from 12 to low value 18 on 21/07/2014 ","numericEntity","N/4/28/2/17 D/2/50/10/2014-07-21"));
    //assertU(adoc("versionId","1250","fileDataEnglish","Profit rates decrease from 12 to low value 18 on 06/11/2014","numericEntity","N/4/28/2/21 N/4/44/2/35 D/2/50/10/2014-11-06"));
    //assertU(adoc("versionId","1250","fileDataEnglish","Profit rates decrease from 12 to low value 18 on 06/11/2014 ","number","N/4/28/2/12","numberTrie","12"));
    
   
    assertU(commit());
  }

  @Test
  public void testQueryFieldsSearch() throws Exception {
  
   assertJQ(req("q", "number:18"), "/response/numFound==1");
  }
  
  @Test
  public void testQueryFieldsRange() throws Exception {
   
   assertJQ(req("q", "number:[8 TO 20]"), "/response/numFound==1");
  }
  
  
  @Test
  public void testQueryFieldsSpanExact() throws Exception {
  
   assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:profit number:12]@~3,3"), "/response/numFound==1");
  }
  
  @Test
  public void testQueryFields1() throws Exception {
   // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
    //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
    
   //assertJQ(req("defType", "span", "q", "[fileDataEnglish:issue number:1234]@~17,17"), "/response/numFound==1"); //running fine
   //assertJQ(req("defType", "span", "q", "[fileDataEnglish:issue date:1989-10-21]@~16,16"), "/response/numFound==1"); 
   
   
   //assertJQ(req("defType", "span", "q", "[fileDataEnglish:document number:12]@~13,13"), "/response/numFound==1"); //working fine 
  
    //assertJQ(req("defType", "span", "q", "[fileDataEnglish:working number:12]@~3,3"), "/response/numFound==1");
   assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:profit number:[8 TO 20]]@~3,3"), "/response/numFound==1");
   
   //assertJQ(req("defType", "span", "q", "[fileDataEnglish:issue numberEntity:#N]@~8,10"), "/response/numFound==1"); 
    
    //assertJQ(req("defType", "span", "q", "[fileDataEnglish:chapter date:*]@~4,4"), "/response/numFound==1");
    //assertJQ(req("defType", "span", "q", "[date:* fileDataEnglish:Today]@~-2,3"), "/response/numFound==1");
    /*//test maxedit > 2
    assertJQ(req("defType", "span", "q", "text1:abcd~3"), "/response/numFound==0");
    assertJQ(req("defType", "span", "q", "text1:abcd~3", "mfd", "3"), "/response/numFound==1");

    //test date field is doing the parsing
    assertJQ(req("defType", "span", "q", "date:'2011-12-01T08:08:08Z/DAY'"), "/response/numFound==1");

    //test date field for range
    assertJQ(req("defType", "span", "q", "t0", "fq", "date:[2011-09-01T12:00:00Z TO 2011-11-01T12:00:00Z]"), "/response/numFound==1");

    //test field specific handling of int
    assertJQ(req("defType", "span", "q", "id:43"), "/response/numFound==1");*/
  }
  
  @Test
  public void testQueryFieldsSpanNegative() throws Exception {
  
   assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:profit number:[8 TO 11]]@~3,3"), "/response/numFound==0");
  }
  
  
  @Test
  public void testQueryFieldsNumberOperator() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[[fileDataEnglish:profit] OR [numericEntity:#D]]"), "/response/numFound==1"); //running fine
  }
  
  
  @Test
  public void testQueryFieldsNumberOperatorOr() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:profit (numericEntity:#M OR number:[8 TO 15])]@~3,3"), "/response/numFound==1"); //running fine
  }
  
  
  @Test
  public void testQueryFieldsNumberOperatorAnd() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:profit (fileDataEnglish:from AND number:[8 TO 15])]@~2,3"), "/response/numFound==1"); //running fine
  }
  
  @Test
  public void testQueryPureBoolean() throws Exception {
    
  assertJQ(req("defType", "ciqparser", "q", "(fileDataEnglish:income AND fileDataEnglish:finance)"), "/response/numFound==1");
  }
  @Test
  public void testQueryFieldsDate() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "span", "q", "[fileDataEnglish:profit date:[2014-03-06 TO 2014-11-06]]@~9,9"), "/response/numFound==1"); //running fine
  }
  
  
  @Test
  public void testQueryFields2() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "span", "q", "[fileDataEnglish:working money:*]@~8,8"), "/response/numFound==1"); //running fine
  }
  
  @Test
  public void testQueryFieldsFail() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "span", "q", "[fileDataEnglish:working money:*]@~9,11"), "/response/numFound==0"); //running fine
  }
  
  @Test
  public void testQueryFields3() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "span", "q", "[number:12 money:*]@~4,4"), "/response/numFound==1"); //running fine
  }
  
  
 
  @Test
  public void testDefaultOperator() throws Exception {
    assertJQ(req("defType", "span", "fq", "text1", "q", "t1 t3",
        "q.op", "AND"), "/response/numFound==0");
    assertJQ(req("defType", "span", "q", "t1 t2",
        "q.op", "OR"), "/response/numFound==2");
    assertJQ(req("defType", "span", "q", "t1 t2"), "/response/numFound==2");
  }

  /** Test that multiterm analysis chain is used for prefix, wildcard and fuzzy */
  public void testMultitermAnalysis() throws Exception {
    assertJQ(req("defType", "span", "q", "FOOBA*"), "/response/numFound==1");
    assertJQ(req("defType", "span", "q", "f\u00F6\u00F6ba*"), "/response/numFound==1");
    assertJQ(req("defType", "span", "q", "f\u00F6\u00F6b?r"), "/response/numFound==1");
    assertJQ(req("defType", "span", "q", "f\u00F6\u00F6bat~1"), "/response/numFound==1");
  }
  
  /** Test negative query */
  public void testNegativeQuery() throws Exception {
    assertJQ(req("defType", "span", "q", "-t0"), "/response/numFound==2");
  }
}
