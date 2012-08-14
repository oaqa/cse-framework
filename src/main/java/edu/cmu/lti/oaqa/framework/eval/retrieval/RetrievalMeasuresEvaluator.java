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

import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.Resource_ImplBase;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.framework.eval.ExperimentKey;
import edu.cmu.lti.oaqa.framework.eval.ExperimentListener;
import edu.cmu.lti.oaqa.framework.eval.Key;

public class RetrievalMeasuresEvaluator extends Resource_ImplBase implements ExperimentListener {
  
  private RetrievalEvalPersistenceProvider persistence;
 
  @Override
  public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> tuples)
          throws ResourceInitializationException {
    String pp = (String) tuples.get("persistence-provider");
    if (pp == null) {
      throw new ResourceInitializationException(new IllegalArgumentException(
              "Must provide a parameter of type <persistence-provider>"));
    }
    this.persistence = BaseExperimentBuilder.loadPersistenceProvider(pp,
            RetrievalEvalPersistenceProvider.class);
    return true;
  }
  
  private final Map<Key, RetrievalCounts> countMap = Maps.newHashMap();

  @Override
  public void process(ExperimentKey experiment) throws AnalysisEngineProcessException {
    persistence.deleteFMeasureEval(experiment);
    Multimap<Key, RetrievalCounts> counts = persistence.retrievePartialCounts(experiment);
    for (Map.Entry<Key, RetrievalCounts> me : counts.entries()) {
      update(me.getKey(), me.getValue());
    }
    doEvaluate();
  }

  private void doEvaluate() throws AnalysisEngineProcessException {
    for (Map.Entry<Key, RetrievalCounts> me : countMap.entrySet()) {
      Key key = me.getKey();
      String eName = getClass().getSimpleName();
      FMeasureEvaluationData eval = evaluate(me.getValue());
      try {
        persistence.insertFMeasureEval(key, eName, eval);
      } catch (Exception e) {
        e.printStackTrace();
        throw new AnalysisEngineProcessException(e);
      }
    }
  }

  private FMeasureEvaluationData evaluate(RetrievalCounts counts) {
    int relevantRetrieved = counts.getRelevantRetrieved();
    int retrieved = counts.getRetrieved();
    float precision = (retrieved != 0) ? relevantRetrieved / (float) retrieved : 0;
    float recall = relevantRetrieved / (float) counts.getRelevant();
    float f1 = EvaluationHelper.getF1(precision, recall);
    float docMAP = counts.getAvep() / counts.getCount();
    float binaryRecall = counts.getBinaryRelevant() / (float) counts.getCount();
    return new FMeasureEvaluationData(precision, recall, f1, docMAP, binaryRecall, counts.getCount());
  }

  private void update(Key key, RetrievalCounts cnt) {
    RetrievalCounts globals = countMap.get(key);
    if (globals == null) {
      globals = new RetrievalCounts();
      countMap.put(key, globals);
    }
    globals.update(cnt);
  }
}