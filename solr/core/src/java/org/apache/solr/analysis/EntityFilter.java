package org.apache.solr.analysis;
/**
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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;



import org.apache.lucene.util.NumberUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

/**
 * A TokenFilter which applies a Pattern to each token in the stream,
 * replacing match occurances with the specified replacement string.
 *
 * <p>
 * <b>Note:</b> Depending on the input and the pattern used and the input
 * TokenStream, this TokenFilter may produce Tokens whose text is the empty
 * string.
 * </p>
 *
 * @version $Id:$
 * @see Pattern
 */
public final class EntityFilter extends TokenFilter {
  private final String entityToStore;
  private final boolean convert;
  private final CharTermAttribute termAtt = input.getAttribute(CharTermAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
  private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
  private final Matcher m;
  private int currentOffset;
 


  /**
   * Constructs an instance to replace either the first, or all occurances
   *
   * @param in the TokenStream to process
   * @param p the pattern to apply to each Token
   * @param entityToStore if true, all matches will be replaced otherwise just the first match.
   * @see Matcher#quoteReplacement
   */
  public EntityFilter(TokenStream in,
                      Pattern p,
                      String entityToStore, boolean convert) {
    super(in);
    this.entityToStore = entityToStore;
    this.convert = convert;
    this.m = p.matcher(termAtt);
    this.currentOffset =0;
  }


  @Override
  public boolean incrementToken() throws IOException {
    if (!input.incrementToken()){ currentOffset=0; return false; }
    m.reset();
    if (m.find()) {
      String transformed="";
      if(entityToStore==null)
        transformed = "#"+ m.group(1); // replacing with entity name
      else if(m.group(1).equalsIgnoreCase(entityToStore))
      {
        if(convert) {
          /*Added this code to convert the date with diff formats 
           * i.e yyyy , yyyy-MM , yyyy-MM-dd, XXXX
           * XXXX-MM, XXXX-XX-dd. It then convert them to long sortable value
           */
          if(entityToStore.equalsIgnoreCase("D")){
            String date=m.group(5);
           
            String[] splitDate=m.group(5).split("-");
            for(int i=0;i<splitDate.length;i++)
            {
              if(splitDate[i].equalsIgnoreCase("XXXX"))
              {splitDate[i]="1400";
             
              }
             if(splitDate[i].equalsIgnoreCase("XX"))
              {splitDate[i]="01";
               
              }
             if(i==0)
              date=splitDate[i];
             else if(splitDate.length!=1 &&  i <(splitDate.length))
             {
              date=date.concat("-".concat(splitDate[i]));
             
             }
            }
           
            SimpleDateFormat df = new SimpleDateFormat();
            if(splitDate.length==3)
            {
             df=new SimpleDateFormat("yyyy-MM-dd");
            }
            else if(splitDate.length==2) 
            {
              df=new SimpleDateFormat("yyyy-MM");
            }
            else if(splitDate.length==1) 
            {
              df=new SimpleDateFormat("yyyy");
            }
          try {
            transformed=NumberUtils.long2sortableStr(df.parse(date).getTime());
          } catch (ParseException e) {
            throw new RuntimeException(" unbale to parse the date during indexing for value :"+m.group(5));
          }
          }
          else{
          double value = Double.parseDouble(m.group(5));
          transformed = NumberUtils.double2sortableStr(value);
          }// changing to sortable string
        }
        else
          transformed = m.group(5);
      }
      //changing the offsets attribute by building incremental offsets..
      offsetAtt.setOffset(currentOffset+=Integer.parseInt(m.group(3)), currentOffset+=Integer.parseInt(m.group(4)));
      //changing the position increment attribute
      posIncrAtt.setPositionIncrement(Integer.parseInt(m.group(2)));
      //replacing the term
      termAtt.setEmpty().append(transformed);
    }
    return true;
  }
}
