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

public class TestSpanQParserPluginDateTrie extends SolrTestCaseJ4 {
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
    assertU(adoc("versionId","1234","fileDataEnglish","this is document 1 after the changes on 06/2011 and we are working on it from 12 days. this cost us 143rs increase in my income is 123","numericEntity","N/3/17/1/1 D/5/22/10/2011-06-01 N/8/31/2/12 M/5/20/3/100"));
    //assertU(adoc("versionId","1235","fileDataEnglish","this is document 1 after the changes on 06/2011 and we are working on it from 12 days. this cost us 143rs increase in my income is 123","numericEntity","N/3/17/1/1 D/5/22/10/2012-06-01 N/8/31/2/12 M/5/20/3/100"));
   
    assertU(commit());
  }
  
  @Test
  public void testQueryFieldsDateExact() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("q", "date:2011-06-01"), "/response/numFound==1"); //running fine
  }
  
  @Test
  public void testQueryFieldsDateRange() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("q", "date:[2010-06-21 TO 2012-10-21]"), "/response/numFound==1"); //running fine
  }
  
  @Test
  public void testQueryFieldsDateAsYearRange() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("q", "date:[2010 TO 2012]"), "/response/numFound==1"); //running fine
  }

  @Test
  public void testQueryFieldsDateTrieRange() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("q", "dateTrie:[2009-06-27 TO 2011-08-27]"), "/response/numFound==1"); //running fine
  }
  
  @Test
  public void testQueryFields1() throws Exception {
   
   assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:document date:*]@~5,5"), "/response/numFound==1");
     
  }
  
  @Test
  public void testQuerySpanDateExact() throws Exception {
   
   assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:document date:2011-06-01]@~5,5"), "/response/numFound==1");
     
  }
  
  @Test
  public void testQueryFieldsSpanDate() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:document date:[2009-01-27 TO 2011-08-27]]@~5,5"), "/response/numFound==1"); //running fine
  }
  
  public void testQueryFieldsSpanDateNegative() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:document date:[2009-01-27 TO 2010-08-27]]@~5,5"), "/response/numFound==0"); //running fine
  }
  
  public void testQueryFieldsSpanDateAnd() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:document (fileDataEnglish:changes AND date:[2009-01-27 TO 2010-08-27])]@~3,3"), "/response/numFound==1"); //running fine
  }
  public void testQueryFieldsSpanDateAndNegative() throws Exception {
    // assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     //assertJQ(req("defType", "span", "q", "[fileDataEnglish:Today date:*]@~1,3"), "/response/numFound==1");
     
    assertJQ(req("defType", "ciqparser", "q", "[fileDataEnglish:document (fileDataEnglish:the AND date:[2009-01-27 TO 2010-08-27])]@~4,5"), "/response/numFound==0"); //running fine
  }
  
}
