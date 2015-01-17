package org.apache.solr.analysis;

/*import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.BaseTokenStreamFactoryTestCase;
import org.junit.Test;*/


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


/* Test case to test the EntityFilterFactory
 * working fine or not 
 *  author Mrinali
 * */
 
/*public class TestEntity extends BaseTokenStreamFactoryTestCase {
  
  
@Test
public void testEntityAll() throws Exception {
  
  HashMap<String,String> para=new HashMap<String,String>();
  para.put("pattern", "(.*)/([0-9]+)/([0-9]+)/([0-9]+)/(.*)?"); // passing the parameters for EntityFilterFactory 
  para.put("entityToStore", "N");                               // defined in schema
  para.put("convertToSortable","2"); 
  
    Reader reader2 = new StringReader("N/8/58/2/19");
    TokenStream stream2 = new MockTokenizer(reader2, MockTokenizer.WHITESPACE, false);
    stream2 = new EntityFilterFactory(
       para).create(stream2);
                   
    assertTokenStreamContents(stream2, 
        new String[] { "19"});
    
  
  }
@Test
public void testDateAll() throws Exception {
  
  HashMap<String,String> para=new HashMap<String,String>();
  para.put("pattern", "(.*)/([0-9]+)/([0-9]+)/([0-9]+)/(.*)?"); // passing the parameters for EntityFilterFactory 
  para.put("entityToStore", "D");                               // defined in schema
  para.put("convertToSortable","2"); 
  
    Reader reader2 = new StringReader("D/8/58/2/2013-12-31");
    TokenStream stream2 = new MockTokenizer(reader2, MockTokenizer.WHITESPACE, false);
    stream2 = new EntityFilterFactory(
       para).create(stream2);
                   
    assertTokenStreamContents(stream2, 
        new String[] { "2013-12-31"});
    
  
  }
  
}*/
