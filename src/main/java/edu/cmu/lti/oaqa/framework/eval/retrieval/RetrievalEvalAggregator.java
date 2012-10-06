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

package edu.cmu.lti.oaqa.framework.eval.retrieval;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.Resource_ImplBase;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.framework.eval.Key;

public class RetrievalEvalAggregator<T> extends Resource_ImplBase implements EvaluationAggregator<T> {

  private RetrievalEvalPersistenceProvider persistence;

  @Override
  public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> tuples)
          throws ResourceInitializationException {
    String pp = (String) tuples.get("persistence-provider");
    if (pp == null) {
      throw new ResourceInitializationException(new IllegalArgumentException(
              "Must provide a parameter of type <persistence-provider>"));
    }
    this.persistence = BaseExperimentBuilder.loadProvider(pp,
            RetrievalEvalPersistenceProvider.class);
    return true;
  }

  @Override
  public void update(Key key, String sequenceId, List<T> docs, List<T> gs, Ordering<T> ordering,
          Function<T, String> toIdString) throws AnalysisEngineProcessException {
    RetrievalCounts cnt = count(docs, gs, ordering, toIdString);
    try {
      persistence.deletePassageAggrEval(key, sequenceId);
      persistence.insertPartialCounts(key, sequenceId, cnt);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

  private RetrievalCounts count(List<T> docs, List<T> gs, Ordering<T> ordering,
          Function<T, String> toIdString) {
    Set<String> docsSet = EvaluationHelper.getStringSet(docs, toIdString);
    Set<String> gsSet = EvaluationHelper.getStringSet(gs, toIdString);
    int relevantRetrieved = EvaluationHelper.intersect(docsSet, gsSet).size();
    int retrieved = docs.size();
    int relevant = gs.size();
    List<String> docsArray = EvaluationHelper.getUniqeDocIdList(docs, ordering, toIdString);
    float avep = EvaluationHelper.getAvgMAP(docsArray, gsSet);
    int binaryRelevant = (relevantRetrieved > 0) ? 1 : 0;
    return new RetrievalCounts(relevantRetrieved, retrieved, relevant, avep, binaryRelevant, 1);
  }

}