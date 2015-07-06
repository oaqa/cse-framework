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

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.ExperimentPersistenceProvider;
import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.async.activemq.ActiveMQQueueProducer;

public abstract class AbstractCollectionReaderProducer extends CollectionReader_ImplBase {

  public static final String COLLECTION_READER_QUEUE_SUFFIX = "-producer";

  private int count = 0;
  
  private Set<String> topics = Sets.newHashSet();

  private ActiveMQQueueProducer producer;
  
  private ExperimentPersistenceProvider persistence;

  @Override
  public void initialize() throws ResourceInitializationException {
    super.initialize();
    String url = (String) getConfigParameterValue("broker-url");
    // String user = (String) getConfigParameterValue("amq-username");
    // String password = (String) getConfigParameterValue("amq-password");
    String pp = (String) getConfigParameterValue("persistence-provider");
    if (pp == null) {
      throw new ResourceInitializationException(new IllegalArgumentException(
             String.format("%s must be provided with a parameter of type <persistence-provider>", getClass().getSimpleName())));
    }
    this.persistence = BaseExperimentBuilder.loadProvider(pp,
            ExperimentPersistenceProvider.class);
    try {
      this.producer = new ActiveMQQueueProducer(url, getUUID()
              + COLLECTION_READER_QUEUE_SUFFIX);
    } catch (JMSException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  public final void getNext(CAS aCAS) throws IOException, CollectionException {
    try {
      DataElement nextElement = getNextFromSource();
      MapMessage message = producer.createMapMessage();
      message.setString("dataset", getDataset());
      message.setString("sequenceId", nextElement.getSequenceId());
      message.setInt("stageId", getStageId());
      producer.send(message);
      topics.add(nextElement.getSequenceId());
      count++;
      persistence.updateExperimentMeta(getUUID(), count);
    } catch (Exception e) {
      throw new CollectionException(e);
    }
  }
  
  protected abstract String getDataset();
  
  protected abstract int getStageId();
  
  protected abstract DataElement getNextFromSource() throws Exception;
  
  protected abstract String getUUID();
  
  @Override
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(count, -1, Progress.ENTITIES) };
  }

  @Override
  public void close() throws IOException {
    persistence.updateExperimentMeta(getUUID(), count, topics);
    Closeables.close(producer, true);
  }

}
