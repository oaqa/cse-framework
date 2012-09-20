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

package edu.cmu.lti.oaqa.framework.collection;

import java.io.IOException;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import com.google.common.collect.Sets;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.ExperimentPersistenceProvider;
import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.types.ExperimentUUID;
import edu.cmu.lti.oaqa.framework.types.InputElement;

public abstract class AbstractCollectionReader extends CollectionReader_ImplBase {

  private String dataset;
  
  private Set<Integer> topics = Sets.newHashSet();

  private int count = 0;

  private AnalysisEngine[] decorators;

  private String experimentUuid;

  private int stageId;
  
  private ExperimentPersistenceProvider persistence;
 
  @Override
  public void initialize() throws ResourceInitializationException {
    this.dataset = (String) getConfigParameterValue("dataset");
    this.experimentUuid = (String) getConfigParameterValue(BaseExperimentBuilder.EXPERIMENT_UUID_PROPERTY);
    this.stageId = (Integer) getConfigParameterValue(BaseExperimentBuilder.STAGE_ID_PROPERTY);
    String decoratorsNames = (String) getConfigParameterValue("decorators");
    if (decoratorsNames != null) {
      this.decorators = BaseExperimentBuilder.createAnnotators(decoratorsNames);
    }
    String pp = (String) getConfigParameterValue("persistence-provider");
    if (pp == null) {
      throw new ResourceInitializationException(new IllegalArgumentException(
             String.format("%s must be provided with a parameter of type <persistence-provider>", getClass().getSimpleName())));
    }
    this.persistence = BaseExperimentBuilder.loadProvider(pp,
            ExperimentPersistenceProvider.class);
  }

  protected abstract DataElement getNextElement() throws Exception;

  public String getDataset() {
    return dataset;
  }

  public String getUUID() {
    return experimentUuid;
  }

  public int getStageId() {
    return stageId;
  }
  
  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    try {
      DataElement nextElement = getNextElement();
      JCas jcas = aCAS.getJCas();
      jcas.setDocumentText(nextElement.getText());
      ExperimentUUID expUuid = new ExperimentUUID(jcas);
      expUuid.setUuid(getUUID());
      expUuid.setStageId(getStageId());
      expUuid.addToIndexes();
      InputElement next = new InputElement(jcas);
      next.setDataset(dataset);
      next.setQuestion(nextElement.getText());
      next.setSequenceId(nextElement.getSequenceId());
      next.setQuuid(nextElement.getQuuid());
      next.addToIndexes();
      decorate(jcas);
      topics.add(nextElement.getSequenceId());
      count++;
      //persistence.updateExperimentMeta(getUUID(), count);
    } catch (Exception e) {
      throw new CollectionException(e);
    }
  }

  protected void decorate(JCas jcas) throws AnalysisEngineProcessException {
    if (decorators != null) {
      for (AnalysisEngine appender : decorators) {
        appender.process(jcas);
      }
    }
  }

  @Override
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(count, -1, Progress.ENTITIES) };
  }
  
  @Override
  public void close() throws IOException {
    persistence.updateExperimentMeta(getUUID(), count, topics);
  }

}
