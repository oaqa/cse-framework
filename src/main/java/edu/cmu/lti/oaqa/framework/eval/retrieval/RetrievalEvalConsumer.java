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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.CasConsumer_ImplBase;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.phase.ProcessingStepUtils;
import edu.cmu.lti.oaqa.ecd.phase.Trace;
import edu.cmu.lti.oaqa.framework.eval.Key;
import edu.cmu.lti.oaqa.framework.types.ExperimentUUID;
import edu.cmu.lti.oaqa.framework.types.ProcessingStep;

public abstract class RetrievalEvalConsumer<T> extends CasConsumer_ImplBase {

  @SuppressWarnings("rawtypes")
  private List<EvaluationAggregator> aggregators;

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    Object aggregatorNames = context.getConfigParameterValue("aggregators");
    if (aggregatorNames != null) {
      this.aggregators = BaseExperimentBuilder.createResourceList(aggregatorNames,
              EvaluationAggregator.class);
    }
  }

  protected abstract Ordering<T> getOrdering();
  
  protected abstract Function<T, String> getToIdStringFct();
  
  protected abstract List<T> getGoldStandard(JCas jcas) throws CASException;

  protected abstract List<T> getResults(JCas jcas) throws CASException;

  @Override
  public void process(CAS aCAS) throws AnalysisEngineProcessException {
    try {
      JCas jcas = aCAS.getJCas();
      ExperimentUUID experiment = ProcessingStepUtils.getCurrentExperiment(jcas);
      AnnotationIndex<Annotation> steps = jcas.getAnnotationIndex(ProcessingStep.type);
      Trace trace = ProcessingStepUtils.getTrace(steps);
      List<T> gs = getGoldStandard(jcas);
      if (gs.size() > 0) {
        List<T> docs = getResults(jcas);
        int sequenceId = ProcessingStepUtils.getSequenceId(jcas);
        for (EvaluationAggregator<T> aggregator : aggregators) {
          Key key = new Key(experiment.getUuid(), trace, experiment.getStageId());
          aggregator.update(key, sequenceId, docs, gs, getOrdering(), getToIdStringFct());
        }
      }
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
}
