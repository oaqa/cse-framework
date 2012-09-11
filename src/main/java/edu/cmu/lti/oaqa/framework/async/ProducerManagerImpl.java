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

package edu.cmu.lti.oaqa.framework.async;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.google.common.collect.Sets;

import edu.cmu.lti.oaqa.cse.driver.AsyncConfiguration;
import edu.cmu.lti.oaqa.framework.async.activemq.ActiveMQQueueConsumer;
import edu.cmu.lti.oaqa.framework.async.activemq.ActiveMQTopicPublisher;
import edu.cmu.lti.oaqa.framework.async.activemq.ActiveMQTopicSubscriber;

public class ProducerManagerImpl implements ProducerManager, MessageListener {
  
  private final String experimentUuid;

  private final ActiveMQQueueConsumer consumer;

  private final ActiveMQTopicPublisher publisher;

  private final ActiveMQTopicSubscriber completionListener;

  private final Set<String> consumers = Sets.newHashSet();

  private CountDownLatch latch;

  int count;

  public ProducerManagerImpl(String experimentUuid, AsyncConfiguration config) throws JMSException {
    this.experimentUuid = experimentUuid;
    String url = config.getBrokerUrl();
    this.consumer = new ActiveMQQueueConsumer(url, experimentUuid
            + COMPLETION_QUEUE_SUFFIX);
    this.publisher = new ActiveMQTopicPublisher(url, Topics.values());
    this.completionListener = new ActiveMQTopicSubscriber(config.getBrokerUrl(), this,
            Topics.PIPELINE_COMPLETE);
  }

  @Override
  public void notifyCloseCollectionReaders() throws Exception {
    publisher.publish(experimentUuid, Topics.COLLECTION_READER_COMPLETE);
  }

  @Override
  public void waitForReaderCompletion(long total) throws JMSException {
    int count = 0;
    consumers.clear();
    long timeout = Long.MAX_VALUE;
    long control = System.currentTimeMillis();
    long window = 0;
    while (count < total) {
      System.out.println("Timeout:" + timeout / 1000);
      MapMessage msg = (MapMessage) consumer.receive(timeout);
      if (msg == null) {
        // TODO: Should wait for the messages form each queue, register which processor is working on each topic!
        System.err.printf("Timed out waiting for completion processed %s of %s (timeout @ %s ms)\n", count, total, timeout);
        break;
      }
      long received = System.currentTimeMillis();
      String consumerUuid = msg.getString("consumerUuid");
      consumers.add(consumerUuid);
      count++;
      window += (received - control);
      float average = window / (float) count;
      timeout = (long) Math.max(average * 10, 10 * 60 * 1000L);
      control = received;
    }
  }

  @Override
  public void notifyNextConfigurationIsReady() throws JMSException {
    publisher.publish(experimentUuid, Topics.DB_CONFIG_READY);
  }

  @Override
  public void close() throws IOException {
    consumer.close();
    publisher.close();
    completionListener.close();
  }

  @Override
  public void waitForProcessCompletion() throws InterruptedException {
    latch = new CountDownLatch(consumers.size());
    latch.await();
  }

  @Override
  public void onMessage(Message msg) {
    TextMessage message = (TextMessage) msg;
    try {
      if (message.getText().equals(experimentUuid)) {
        if (latch != null) {
          latch.countDown();
        }
      }
    } catch (JMSException e) {
      System.err.println("Unable to process message: " + message);
    }
  }
}
