package org.apache.solr.constants;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

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

public class SolrConstants {
  
  public static String entityFlag="N";
  public static String constantScoring="N";
  public static HashMap<String,String> prop= new HashMap<String, String>();
    
  
  
  
  public static void loadProperties(String path) throws IOException
  {
    if(path!=null){
    BufferedReader reader = new BufferedReader(new FileReader(path));
    String line = null;
    while ((line = reader.readLine()) != null){
      String val[]=line.split("=");
      prop.put(val[0], val[1]);
      
      
    }
    }
    else
    {
      throw new IOException("Cannot load props file");
    }
  }
  
  public static void intialize()
  {
    if(prop.get("entityFlag")!=null)
     SolrConstants.entityFlag=prop.get("entityFlag");
    if(prop.get("constantScoring")!=null)
     SolrConstants.constantScoring=prop.get("constantScoring");
        
  }
  
  /*public static void main(String args[]) throws IOException
  {
    String path="D:\\revised content\\props.txt";
    loadProperties(path);
    intialize();
    System.out.println("entityFlag val ::"+MyApp.entityFlag);
    System.out.println("scoring val ::"+MyApp.constantScoring);
  }*/

  
}
