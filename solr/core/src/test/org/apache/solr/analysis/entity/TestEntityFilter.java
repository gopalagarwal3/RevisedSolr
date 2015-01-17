package org.apache.solr.analysis.entity;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.EntityFilter;
import org.apache.solr.util.NumberUtils;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
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

public class TestEntityFilter extends BaseTokenStreamTestCase{
  public void testNumericTokensFromFilter() throws Exception {
    String input = "N/2/6/2/10 N/8/57/5/20549 N/5/42/2/13";
    TokenStream ts = new EntityFilter
            (new MockTokenizer(new StringReader(input), MockTokenizer.WHITESPACE, false),
                    Pattern.compile("(.*)/([0-9]+)/([0-9]+)/([0-9]+)/(.*)?"),
                    "N", false);
    assertTokenStreamContents(ts, 
        new String[] { "10", "20549", "13" });
  }

  public void testNumericTokensFromFilterWithSort() throws Exception {
    String input = "N/2/6/2/10 N/8/57/5/20549 N/5/42/2/13";
    TokenStream ts = new EntityFilter
        (new MockTokenizer(new StringReader(input), MockTokenizer.WHITESPACE, false),
            Pattern.compile("(.*)/([0-9]+)/([0-9]+)/([0-9]+)/(.*)?"),
            "N", true);
    assertTokenStreamContents(ts,
        new String[] {NumberUtils.double2sortableStr(10), NumberUtils.double2sortableStr(20549), NumberUtils.double2sortableStr(13) });
  }
  
  public void testMoneyTokensFromFilter() throws Exception {
    String input = "M/2/4/2/15 M/8/47/6/25.00 M/10/48/13/9.24E7";
    TokenStream ts = new EntityFilter
            (new MockTokenizer(new StringReader(input), MockTokenizer.WHITESPACE, false),
                    Pattern.compile("(.*)/([0-9]+)/([0-9]+)/([0-9]+)/(.*)?"),
                    "M", false);
    assertTokenStreamContents(ts, 
        new String[] { "15" , "25.00" , "9.24E7" });
  }
  
  public void testPercentageTokensFromFilter() throws Exception {
    String input = "P/4/23/4/100 P/6/19/5/2.96 P/1/1/1/-1";
    TokenStream ts = new EntityFilter
            (new MockTokenizer(new StringReader(input), MockTokenizer.WHITESPACE, false),
                    Pattern.compile("(.*)/([0-9]+)/([0-9]+)/([0-9]+)/(.*)?"),
                    "P", true);
    assertTokenStreamContents(ts, 
        new String[] { NumberUtils.double2sortableStr(100), NumberUtils.double2sortableStr(2.96),NumberUtils.double2sortableStr(-1)});
  }
  
  public void testDateTokensFromFilter() throws Exception {
    String input = " D/8/39/4/1934-01-01 D/4/5/17/2013-12 D/4/5/17/2013 D/4/19/8/XXXX-03-01 D/4/19/8/XXXX-03 D/4/19/8/XXXX-XX-01";
    SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat df1=new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat df2=new SimpleDateFormat("yyyy");
    TokenStream ts = new EntityFilter
            (new MockTokenizer(new StringReader(input), MockTokenizer.WHITESPACE, false),
                    Pattern.compile("(.*)/([0-9]+)/([0-9]+)/([0-9]+)/(.*)?"),
                    "D", true);
    Long l1=df.parse("1934-01-01").getTime();
    Long l2=df1.parse("2013-12").getTime();
    Long l3=df2.parse("2013").getTime();
    Long l4=df.parse("1400-03-01").getTime();
    Long l5=df1.parse("1400-03").getTime();
    Long l6=df.parse("1400-01-01").getTime();
    assertTokenStreamContents(ts, 
        new String[] { NumberUtils.long2sortableStr(l1) , NumberUtils.long2sortableStr(l2),  NumberUtils.long2sortableStr(l3), NumberUtils.long2sortableStr(l4),
        NumberUtils.long2sortableStr(l5), NumberUtils.long2sortableStr(l6)});
  }
  
  public void testRatioTokensFromFilter() throws Exception {
    String input = "R/5/29/5/10-00 R/22/143/5/4.5-1 R/26/123/5/12-01";
    TokenStream ts = new EntityFilter
            (new MockTokenizer(new StringReader(input), MockTokenizer.WHITESPACE, false),
                    Pattern.compile("(.*)/([0-9]+)/([0-9]+)/([0-9]+)/(.*)?"),
                    "R", false);
    assertTokenStreamContents(ts, 
        new String[] { "10-00", "4.5-1", "12-01" });
  }
}
