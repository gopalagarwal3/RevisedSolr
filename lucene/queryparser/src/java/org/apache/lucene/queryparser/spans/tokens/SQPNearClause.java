package org.apache.lucene.queryparser.spans.tokens;

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

public class SQPNearClause extends SQPClause{
  
  
  public static final Boolean UNSPECIFIED_IN_ORDER = null;

  public static final int NEAR_DEFAULT = 0;
  private final TYPE type;
  
  private final Boolean inOrder;
  private final boolean hasParams;
  private final int nearPre;
  private final int nearPost;

  //the offset at which the contents of this clause start
  private final int charStartOffset;
  //the character offset at which the contents of this clause end
  private final int charEndOffset;
  
  //a b "the quick" brown
  //charStartOffset=5
  //charEndOffset=13
  public SQPNearClause(int tokenStartOffset, int tokenEndOffset, 
      int charStartOffset, int charEndOffset, TYPE type, 
      boolean hasParams, Boolean inOrder, int nearPre, int nearPost){
    super(tokenStartOffset, tokenEndOffset);
    this.type = type;
    this.hasParams = hasParams;
    this.inOrder = inOrder;
    this.nearPre= nearPre;
    this.nearPost=nearPost;
    this.charStartOffset = charStartOffset;
    this.charEndOffset = charEndOffset;
  }

  public TYPE getType() {
    return type;
  }

  public int getnearPre() {
    return nearPre;
  }

  public int getnearPost() {
    return nearPost;
  }
  public int getCharStartOffset(){
    return charStartOffset;
  }
  
  public int getCharEndOffset(){
    return charEndOffset;
  }
  
  public Boolean getInOrder() {
    return inOrder;
  }

  public boolean hasParams() {
    return hasParams;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (hasParams ? 1231 : 1237);
    result = prime * result + ((inOrder == null) ? 0 : inOrder.hashCode());
    result = prime * result + nearPost;
    result = prime * result + nearPre;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof SQPNearClause)) {
      return false;
    }
    SQPNearClause other = (SQPNearClause) obj;
    if (hasParams != other.hasParams) {
      return false;
    }
    if (inOrder == null) {
      if (other.inOrder != null) {
        return false;
      }
    } else if (!inOrder.equals(other.inOrder)) {
      return false;
    }
    if (nearPost != other.nearPost) {
      return false;
    }
    if (nearPre != other.nearPre) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SQPNearClause [type=");
    builder.append(type);
    builder.append(", inOrder=");
    builder.append(inOrder);
    builder.append(", hasParams=");
    builder.append(hasParams);
    builder.append(", nearPre=");
    builder.append(nearPre);
    builder.append(", nearPost=");
    builder.append(nearPost);
    builder.append("]");
    return builder.toString();
  }  
 
}
