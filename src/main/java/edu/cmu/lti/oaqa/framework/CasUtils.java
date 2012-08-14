/*
 *  Copyright 2012 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package edu.cmu.lti.oaqa.framework;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public final class CasUtils {

  private CasUtils() {}
  
  public static Annotation getFirst(JCas jcas, String typeName) {
    TypeSystem ts = jcas.getTypeSystem();
    Type type = ts.getType(typeName);
    AnnotationIndex<Annotation> index = jcas.getAnnotationIndex(type);
    FSIterator<Annotation> iterator = index.iterator();
    if (iterator.hasNext()) {
      return (Annotation) iterator.next();
    }
    return null;
  }

  public static Annotation getFirst(JCas jcas, Type type) {
    AnnotationIndex<Annotation> index = jcas.getAnnotationIndex(type);
    FSIterator<Annotation> iterator = index.iterator();
    if (iterator.hasNext())
      return (Annotation) iterator.next();
    return null;
  }

  public static Type getType(JCas jcas, String typeName) {
    TypeSystem ts = jcas.getTypeSystem();
    return ts.getType(typeName);
  }
}
