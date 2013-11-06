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

package edu.cmu.lti.oaqa.framework.eval;

import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.fit.component.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.phase.ProcessingStepUtils;
import edu.cmu.lti.oaqa.ecd.phase.Trace;
import edu.cmu.lti.oaqa.framework.types.ExperimentUUID;
import edu.cmu.lti.oaqa.framework.types.ProcessingStep;

public class TraceConsumer extends CasConsumer_ImplBase {

  private Set<ExperimentKey> experiments = Sets.newHashSet();

  private List<TraceListener> listeners = Lists.newArrayList();

  private List<ExperimentListener> experimentListeners = Lists.newArrayList();

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    Object listenerNames = (Object) context.getConfigParameterValue("listeners");
    if (listenerNames != null) {
      listeners = BaseExperimentBuilder.createResourceList(listenerNames, TraceListener.class);
    }
    Object experimentListenerNames = context.getConfigParameterValue("experiment-listeners");
    if (experimentListenerNames != null) {
      this.experimentListeners = BaseExperimentBuilder.createResourceList(experimentListenerNames, ExperimentListener.class);
    }
  }

  /**
   * Reads the results from the retrieval phase from the DOCUMENT and the DOCUEMNT_GS views of the
   * JCAs, and generates and evaluates them using the evaluate method from the FMeasureConsumer
   * class.
   */
  @Override
  public void process(CAS aCAS) throws AnalysisEngineProcessException {
    try {
      JCas jcas = aCAS.getJCas();
      ExperimentUUID experiment = ProcessingStepUtils.getCurrentExperiment(jcas);
      AnnotationIndex<Annotation> steps = jcas.getAnnotationIndex(ProcessingStep.type);
      String uuid = experiment.getUuid();
      Trace trace = ProcessingStepUtils.getTrace(steps);
      Key key = new Key(uuid, trace, experiment.getStageId());
      experiments.add(new ExperimentKey(key.getExperiment(), key.getStage()));
      for (TraceListener listener : listeners) {
        listener.process(key, jcas);
      }
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    for (TraceListener listener : listeners) {
      listener.collectionProcessComplete();
    }
    for (ExperimentListener listener : experimentListeners) {
      for (ExperimentKey experiment : experiments) {
        listener.process(experiment);
      }
    }
  }
}
