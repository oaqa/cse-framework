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
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.async.ProducerManager;
import edu.cmu.lti.oaqa.framework.async.Topics;
import edu.cmu.lti.oaqa.framework.async.activemq.ActiveMQQueueConsumer;
import edu.cmu.lti.oaqa.framework.async.activemq.ActiveMQQueueProducer;
import edu.cmu.lti.oaqa.framework.async.activemq.ActiveMQTopicSubscriber;
import edu.cmu.lti.oaqa.framework.types.ExperimentUUID;
import edu.cmu.lti.oaqa.framework.types.InputElement;

public abstract class AbstractCollectionReaderConsumer extends CollectionReader_ImplBase implements
        MessageListener {

  int nextInput;

  private String consumerUuid;

  private String experimentUuid;

  private DataElement nextElement;

  private AnalysisEngine[] decorators;

  private ActiveMQQueueConsumer consumer;

  private ActiveMQQueueProducer producer;

  private ActiveMQTopicSubscriber closeListener;

  private boolean processing = true;

  private int stageId;
  
  private String lastSequenceId;
  
  private String dataset;

  @Override
  public void initialize() throws ResourceInitializationException {
    // String user = (String) getConfigParameterValue("amq-username");
    // String password = (String) getConfigParameterValue("amq-password");
    String url = (String) getConfigParameterValue("broker-url");
    String prefetchUrl = url + "?jms.prefetchPolicy.queuePrefetch=0";
    this.consumerUuid = UUID.randomUUID().toString();
    this.experimentUuid = (String) getConfigParameterValue(BaseExperimentBuilder.EXPERIMENT_UUID_PROPERTY);
    this.stageId = (Integer) getConfigParameterValue(BaseExperimentBuilder.STAGE_ID_PROPERTY);

    try {
      initDecorators();
      this.closeListener = new ActiveMQTopicSubscriber(url, this, Topics.COLLECTION_READER_COMPLETE);
      this.consumer = new ActiveMQQueueConsumer(prefetchUrl, this.experimentUuid
              + AbstractCollectionReaderProducer.COLLECTION_READER_QUEUE_SUFFIX);
      this.producer = new ActiveMQQueueProducer(url, this.experimentUuid
              + ProducerManager.COMPLETION_QUEUE_SUFFIX);
    } catch (Exception e) {
      throw new ResourceInitializationException(e);
    }
  }

  private void initDecorators() {
    nextInput = 0;
    String decoratorsNames = (String) getConfigParameterValue("decorators");
    if (decoratorsNames != null) {
      this.decorators = BaseExperimentBuilder.createAnnotators(decoratorsNames);
    }
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    if (lastSequenceId != null) {
      try {
        notifyProcessed(dataset, lastSequenceId);
      } catch (JMSException e) {
        e.printStackTrace();
      }
    }
    return waitForNext();
  }

  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    try {
      nextInput++;
      JCas jcas = aCAS.getJCas();
      jcas.setDocumentText(nextElement.getText());
      ExperimentUUID expUuid = new ExperimentUUID(jcas);
      expUuid.setUuid(experimentUuid);
      expUuid.setStageId(stageId);
      expUuid.addToIndexes();
      String sequenceId = nextElement.getSequenceId();
      InputElement next = new InputElement(jcas);
      next.setDataset(nextElement.getDataset());
      next.setQuestion(nextElement.getText());
      next.setSequenceId(sequenceId);
      next.addToIndexes();
      decorate(jcas);
      this.dataset = nextElement.getDataset();
      this.lastSequenceId = sequenceId;
    } catch (Exception e) {
      throw new CollectionException(e);
    }
  }

  private boolean waitForNext() throws CollectionException {
    if (!processing) {
      return false;
    }
    try {
      Message msg = consumer.receive();
      try {
        MapMessage map = (MapMessage) msg;
        if (map == null) {
          // consumer is already closed
          return false;
        }
        int stageId = map.getInt("stageId");
        if (this.stageId != stageId) {
          throw new IllegalStateException(String.format("Received stage id %s expected %s ",
                  stageId, this.stageId));
        }
        nextElement = getDataElement(map);
        // TODO: CONCEPTUAL ERROR, THIS ACKNOWLEDGES THAT THE TOPIC HAS BEEN RECEIVED AND DECORATED, 
        // NOT PROCESSED
        msg.acknowledge();
        return true;
      } catch (Exception e) {
        consumer.recover();
        Throwables.propagateIfInstanceOf(e, CollectionException.class);
        throw new CollectionException(e);
      }
    } catch (JMSException e) {
      throw new CollectionException(e);
    }
  }

  protected abstract DataElement getDataElement(MapMessage map) throws Exception;

  protected void decorate(JCas jcas) throws AnalysisEngineProcessException {
    if (decorators != null) {
      for (AnalysisEngine appender : decorators) {
        appender.process(jcas);
      }
    }
  }

  @Override
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(nextInput, -1, Progress.ENTITIES) };
  }

  private void notifyProcessed(String dataset, String lastSequenceId) throws JMSException {
    MapMessage message = producer.createMapMessage();
    message.setString("dataset", dataset);
    message.setString("sequenceId", lastSequenceId);
    message.setString("consumerUuid", getConsumerUuid());
    producer.send(message);
  }

  private String getConsumerUuid() {
    return consumerUuid;
  }

  @Override
  public void onMessage(Message msg) {
    TextMessage message = (TextMessage) msg;
    try {
      // TODO: Synchronize lock?
      if (message.getText().equals(experimentUuid)) {
        processing = false;
        Closeables.closeQuietly(consumer);
      }
    } catch (Exception e) {
      System.err.println("Unable to process message: " + message);
    }
  }

  @Override
  public void close() throws IOException {
    System.out.printf("(%s) Closing connections!!\n", stageId);
    Closeables.closeQuietly(consumer);
    Closeables.closeQuietly(producer);
    Closeables.closeQuietly(closeListener);
  }

}
