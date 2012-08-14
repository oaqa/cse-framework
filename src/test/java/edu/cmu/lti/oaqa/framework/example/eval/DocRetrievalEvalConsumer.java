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

package edu.cmu.lti.oaqa.framework.example.eval;

import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

import edu.cmu.lti.oaqa.framework.eval.retrieval.RetrievalEvalConsumer;
import edu.cmu.lti.oaqa.framework.types.OutputElement;

public class DocRetrievalEvalConsumer extends RetrievalEvalConsumer<OutputElement> {

  @Override
  protected List<OutputElement> getGoldStandard(JCas jcas) throws CASException {
    return Lists.newArrayList();
  }

  @Override
  protected List<OutputElement> getResults(JCas jcas) throws CASException {
    return loadDocumentSet(jcas);
  }
  
  private List<OutputElement> loadDocumentSet(JCas jcas) {
    List<OutputElement> result = Lists.newArrayList();
    Iterator<?> it = jcas.getJFSIndexRepository().getAllIndexedFS(OutputElement.type);
    while (it.hasNext()) {
      result.add((OutputElement) it.next());
    }
    return result;
  }
  
  @Override
  protected Ordering<OutputElement> getOrdering() {
    return new Ordering<OutputElement>() {
      public int compare(OutputElement left, OutputElement right) {
        int rankDiff = Ints.compare(left.getSequenceId(),right.getSequenceId());
        if (rankDiff != 0) {
          return rankDiff;
        }
        return left.getAnswer().compareTo(right.getAnswer());
      }
    };
  }

  @Override
  protected Function<OutputElement, String> getToIdStringFct() {
    return new Function<OutputElement, String>() {
      @Override
      public String apply(OutputElement input) {
        return input.getAnswer();
      }
    };
  }

}
