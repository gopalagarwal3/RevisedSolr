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

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Factory for {@link org.apache.solr.analysis.EntityFilter}.
 * <pre class="prettyprint" >
 * &lt;fieldType name="text_ptnreplace" class="solr.TextField" positionIncrementGap="100"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.KeywordTokenizerFactory"/&gt;
 *     &lt;filter class="solr.PatternReplaceFilterFactory" pattern="([^a-z])" replacement=""
 *             replace="all"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 * @version $Id$
 * @see org.apache.solr.analysis.EntityFilter
 */
public class EntityFilterFactory extends TokenFilterFactory {
  Pattern p;
  String entityToStore;
  boolean convert =false;

  public EntityFilterFactory(Map<String, String> args) {
      super(args);
      p = getPattern(args, "pattern");
      entityToStore = args.get("entityToStore");
      convert = "1".equals(args.get("convertToSortable"));
/*      if (args.isEmpty()) {
          throw new IllegalArgumentException("Unknown parameters: " + args);
      }*/
  }

      @Override
      public EntityFilter create(TokenStream input) {
          return new EntityFilter(input, p,entityToStore,convert);
      }

  }
