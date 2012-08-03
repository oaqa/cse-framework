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

import java.util.Iterator;

import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * 
 * @author Zi Yang <ziy@cs.cmu.edu>
 * 
 */


public class BaseJCasHelper {

  public static Annotation getAnnotation(JCas jcas, int typeID) {

    AnnotationIndex<Annotation> index = jcas.getAnnotationIndex(typeID);
    Iterator<Annotation> it = index.iterator();
    Annotation annotation = null;
    if (it.hasNext()) {
      annotation = it.next();
    }
    return annotation;
  }

  public static TOP getFS(JCas jcas, int typeID) {

    Iterator<TOP> it = jcas.getJFSIndexRepository().getAllIndexedFS(typeID);
    TOP fs = null;
    if (it.hasNext()) {
      fs = it.next();
    }
    return fs;
  }

  /**
   * 
   * @param jcas
   *          the CAS in which we'll store this annotation
   * @param annotation
   *          the annotation to be stored. Removes any instances of this annotation type fi
   */
  public static void storeAnnotation(JCas jcas, TOP annotation, int typeID) {

    Iterator<TOP> it = jcas.getJFSIndexRepository().getAllIndexedFS(typeID);
    while (it.hasNext()) {
      TOP oldAnnotation = it.next();
      oldAnnotation.removeFromIndexes();
    }
    annotation.addToIndexes();
  }

  /**
   * 
   * @param jcas
   *          the CAS in which we'll store this annotation
   * @param fs
   *          the annotation to be stored. Removes any instances of this annotation type fi
   */
  @SuppressWarnings("unchecked")
  public static <T extends TOP> void storeFS(JCas jcas, T fs) {

    Iterator<TOP> it = jcas.getJFSIndexRepository().getAllIndexedFS(T.type);
    while (it.hasNext()) {
      T oldAnnotation = (T) it.next();
      oldAnnotation.removeFromIndexes();
    }
    fs.addToIndexes();
  }

  public static NonEmptyFSList addToFSList(JCas aJCas, FSList list, TOP item) {

    NonEmptyFSList result = new NonEmptyFSList(aJCas);
    result.setHead(item);
    result.setTail(list);
    return result;
  }

  public static <T extends TOP> Iterable<T> fsIterator(final FSList fsList) {
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return new Iterator<T>() {
          FSList remainingList = fsList;

          @Override
          public boolean hasNext() {
            return remainingList instanceof NonEmptyFSList;
          }

          @SuppressWarnings("unchecked")
          @Override
          public T next() {
            NonEmptyFSList currentList = (NonEmptyFSList) remainingList;
            T head = (T) currentList.getHead();

            remainingList = currentList.getTail();
            return head;
          }

          @Override
          public void remove() {
            throw new RuntimeException("Can't remove from an FSListIterator.");
          }

        };
      }

    };
  }
}
