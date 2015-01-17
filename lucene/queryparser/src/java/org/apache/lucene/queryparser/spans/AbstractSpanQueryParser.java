package org.apache.lucene.queryparser.spans;
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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.constants.luceneConstants;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.spans.tokens.SQPBoostableToken;
import org.apache.lucene.queryparser.spans.tokens.SQPClause;
import org.apache.lucene.queryparser.spans.tokens.SQPNearClause;
import org.apache.lucene.queryparser.spans.tokens.SQPNotNearClause;
import org.apache.lucene.queryparser.spans.tokens.SQPOrClause;
import org.apache.lucene.queryparser.spans.tokens.SQPRangeTerm;
import org.apache.lucene.queryparser.spans.tokens.SQPRegexTerm;
import org.apache.lucene.queryparser.spans.tokens.SQPTerm;
import org.apache.lucene.queryparser.spans.tokens.SQPTerminal;
import org.apache.lucene.queryparser.spans.tokens.SQPToken;
import org.apache.lucene.queryparser.spans.tokens.SQPBooleanOpToken;



import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.queryparser.spans.tokens.SQPField;

public abstract class AbstractSpanQueryParser extends SpanQueryParserBase {

  String currentField;
  
  @Override
  abstract public Query parse(String s) throws ParseException;


  /**
   *Recursively called to parse a span query
   * 
   * This assumes that there are no FIELD tokens and no BOOLEAN operators
   *  tokens
   *  parentClause
   * @return SpanQuery
   *  ParseException
   */
  protected SpanQuery _parsePureSpanClause(final List<SQPToken> tokens, 
      String field, SQPClause parentClause) 
          throws ParseException{

    int start = parentClause.getTokenOffsetStart();
    int end = parentClause.getTokenOffsetEnd();

    //test if special handling needed for spannear with one component?
    if (end-start == 1){

      if (parentClause instanceof SQPNearClause){
        SQPNearClause nc = (SQPNearClause)parentClause;
        SQPToken t = tokens.get(start);
        if (t instanceof SQPTerm){

          SpanQuery ret = trySpecialHandlingForSpanNearWithOneComponent(field, (SQPTerm)t, nc);
          if (ret != null){
            if (parentClause.getBoost() != SpanQueryParserBase.UNSPECIFIED_BOOST){
              ret.setBoost(parentClause.getBoost());
            }
            return ret;
          }
        }
      }
    }
    int conj=0;
    int mod=0;
   
    List<SpanQuery> queries = new ArrayList<SpanQuery>();
    int i = start;
    while (i < end){
      SQPToken t = tokens.get(i);
      SpanQuery q = null;
      if (t instanceof SQPClause){
        SQPClause c = (SQPClause)t;
        q = _parsePureSpanClause(tokens, field, c);
        i = c.getTokenOffsetEnd();
      } 
      /*This else if is added to handle the boolean OR , AND 
       * inside our span queries. For OR we are not doing anything 
       * as it creates OR clause by default . For AND , we are building SPAN NEAR with max distance
       */
      else if (t instanceof  SQPBooleanOpToken )
      { SQPBooleanOpToken clause=(SQPBooleanOpToken)t;
        if(clause.getType()==1)
        { 
         conj=clause.getType();
         mod=SpanQueryParser.MOD_NONE;
         
         i++;
        }
        else
         i++;
        }
      else if (t instanceof SQPTerminal){
        
        /* changes done to do range search from number field to 
         * numberTrie field
         */
        if(luceneConstants.entityFlag.equalsIgnoreCase("Y")){
          
        if(t instanceof SQPRangeTerm && currentField.equalsIgnoreCase("number"))
          currentField="numberTrie";
        else if (t instanceof SQPRangeTerm && currentField.equalsIgnoreCase("date"))
          currentField="dateTrie";
        else if(t instanceof SQPRangeTerm && currentField.equalsIgnoreCase("money"))
          currentField="moneyTrie";
        else if (t instanceof SQPRangeTerm && currentField.equalsIgnoreCase("percentage"))
          currentField="percentageTrie";
        // changes end here
        }
        if(currentField != null && field != currentField)
        {q = buildSpanTerminal(currentField, (SQPTerminal)t);}
        else
        {q = buildSpanTerminal(field, (SQPTerminal)t);}
        i++;
      } 
      else if (t instanceof SQPField) //Adding this condition to avoid exception 
      { 
        currentField = ((SQPField) t).getField();
        i++;}
      else {
        throw new ParseException("Can't process field, boolean operators or a match all docs query in a pure span.");
      }
      if (q != null){
        queries.add(q);
      }
    }
    if (queries == null || queries.size() == 0){
      return getEmptySpanQuery();
    }
    if(conj==1)
    {
      /* This case is to handle Span AND operator case inside Span
       * i.e  boolean ANd inside Span query 
       */
      
      return buildSpanQueryClause(queries, new SQPNearClause(parentClause.getTokenOffsetStart(), parentClause.getTokenOffsetEnd(),
          parentClause.getTokenOffsetStart(), parentClause.getTokenOffsetEnd(), SQPClause.TYPE.BRACKET, false, false, Integer.MIN_VALUE+1, Integer.MAX_VALUE));
    }
    else
     return buildSpanQueryClause(queries, parentClause);
  }   


  private SpanQuery trySpecialHandlingForSpanNearWithOneComponent(String field, 
      SQPTerm token, SQPNearClause clause) 
          throws ParseException{

    int nearPre = (clause.getnearPre() == SpanQueryParserBase.UNSPECIFIED_SLOP) ? getPhraseSlop() : clause.getnearPre();
    int nearPost= (clause.getnearPost() == SpanQueryParserBase.UNSPECIFIED_SLOP) ? getPhraseSlop() : clause.getnearPost();   
    
    boolean order = clause.getInOrder() == null ? true : clause.getInOrder().booleanValue();

    SpanQuery ret = (SpanQuery)specialHandlingForSpanNearWithOneComponent(field, 
        token.getString(), order, nearPre, nearPost);
    return ret;

  }

  protected SpanQuery buildSpanTerminal(String field, SQPTerminal token) throws ParseException{
    Query q = null;
    if (token instanceof SQPRegexTerm){
      q = getRegexpQuery(field, ((SQPRegexTerm)token).getString());
    } else if (token instanceof SQPTerm){
      q = buildAnySingleTermQuery(field, ((SQPTerm)token).getString(), ((SQPTerm)token).isQuoted());
    } else if (token instanceof SQPRangeTerm){
      SQPRangeTerm rt = (SQPRangeTerm)token;
      q = getRangeQuery(field, rt.getStart(), rt.getEnd(), 
          rt.getStartInclusive(), rt.getEndInclusive());
    }
    if (q != null && token instanceof SQPBoostableToken){
      float boost = ((SQPBoostableToken)token).getBoost();
      if (boost != SpanQueryParserBase.UNSPECIFIED_BOOST){
        q.setBoost(boost);
      } 
    }
    if (q != null && q instanceof SpanQuery){
      return (SpanQuery)q;
    }
    return null;
  }

  private SpanQuery buildSpanQueryClause(List<SpanQuery> queries, SQPClause clause) 
      throws ParseException {
    SpanQuery q = null;
    if (clause instanceof SQPOrClause){
      q = buildSpanOrQuery(queries);
    } else if (clause instanceof SQPNearClause){

      int nearPre = ((SQPNearClause)clause).getnearPre();
      int nearPost = ((SQPNearClause)clause).getnearPost();
      if (nearPre == UNSPECIFIED_SLOP){
        nearPre = getPhraseSlop();
      }

      Boolean inOrder = ((SQPNearClause)clause).getInOrder();
      boolean order = false;
      if (inOrder == null){
        order = nearPre > 0 ? false : true; 
      } else {
        order = inOrder.booleanValue();
      }
      q = buildSpanNearQuery(queries, order,
          nearPre,nearPost);
    } else if (clause instanceof SQPNotNearClause){
      q = buildSpanNotNearQuery(queries, 
          ((SQPNotNearClause)clause).getNotPre(), 
          ((SQPNotNearClause)clause).getNotPost());

    } else {
      //throw early and loudly. This should never happen.
      throw new IllegalArgumentException("clause not recognized: "+clause.getClass());
    }

    if (clause.getBoost() != UNSPECIFIED_BOOST){
      q.setBoost(clause.getBoost());      
    }
    //now update boost if clause only had one child
    if (q.getBoost() == UNSPECIFIED_BOOST && 
        clause.getBoost() != UNSPECIFIED_BOOST && (
            q instanceof SpanTermQuery ||
            q instanceof SpanMultiTermQueryWrapper)){
      q.setBoost(clause.getBoost());
    }

    return q;
  }
}
