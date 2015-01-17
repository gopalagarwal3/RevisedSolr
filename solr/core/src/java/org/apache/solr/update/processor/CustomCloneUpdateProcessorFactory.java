/**
 * 
 */
package org.apache.solr.update.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.AddUpdateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 *
 */
public class CustomCloneUpdateProcessorFactory extends FieldMutatingUpdateProcessorFactory {
  
  private static final Logger log = LoggerFactory.getLogger(RegexReplaceProcessorFactory.class);

  private static final String Entity_PARAM = "entity";

  private String entity;
 
  // by default, literalReplacementEnabled is set to true to allow backward compatibility
  
  @Override
  public void init(NamedList args) {
    Object entityParam = args.remove(Entity_PARAM);
    if (entityParam == null) {
      throw new SolrException(ErrorCode.SERVER_ERROR, 
                              "Missing required init parameter: " + Entity_PARAM);
    }
    
    try
    {
      entity=entityParam.toString();
    }catch (Exception e) {
      throw new SolrException(ErrorCode.SERVER_ERROR, 
          "Invalid parameter: " + entityParam, e);
      }
    super.init(args);
  }
  
  @Override
  protected FieldMutatingUpdateProcessor.FieldNameSelector 
    getDefaultSelector(final SolrCore core) {

    return FieldMutatingUpdateProcessor.SELECT_NO_FIELDS;

  }

  @Override
  public UpdateRequestProcessor getInstance(SolrQueryRequest req, SolrQueryResponse rsp, UpdateRequestProcessor next)
  {
    return new FieldValueMutatingUpdateProcessor(getSelector(), next) {
     
      String [] splitValue;
      List parsedValue = new ArrayList();;
      @Override
     
      protected Object mutateValue(final Object src) {
       if(src instanceof CharSequence)
       {
         
         String val =src.toString();
         splitValue= val.split("\\s");
         
        
         for(int i=0;i<splitValue.length;i++){
           if(splitValue[i].startsWith(entity))
          {
           int length=splitValue[i].length();
           int index1=splitValue[i].lastIndexOf("/");
         //  System.out.println("last index of /is "+index1);
          // System.out.println("substring ::"+splitValue[i].substring(index1+1, length));
          parsedValue.add(splitValue[i].substring(index1+1, length));
           
          }
        
       }
      
       }
       return parsedValue;
      }
    };
  }
}
