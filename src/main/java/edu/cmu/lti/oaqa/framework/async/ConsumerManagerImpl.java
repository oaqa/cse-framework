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
import java.util.concurrent.CountDownLatch;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import edu.cmu.lti.oaqa.cse.driver.CSEFrameworkConfiguration;
import edu.cmu.lti.oaqa.framework.async.activemq.ActiveMQTopicPublisher;
import edu.cmu.lti.oaqa.framework.async.activemq.ActiveMQTopicSubscriber;

public class ConsumerManagerImpl implements ConsumerManager, MessageListener {

  private ActiveMQTopicSubscriber closeListener;

  private final ActiveMQTopicPublisher publisher;
  
  private String uuid;

  private CountDownLatch latch;
  
  public ConsumerManagerImpl(String uuid, CSEFrameworkConfiguration config) throws JMSException {
    this.uuid = uuid;
    this.closeListener = new ActiveMQTopicSubscriber(config.getBrokerUrl(), this, Topics.DB_CONFIG_READY);
    this.publisher = new ActiveMQTopicPublisher(config.getBrokerUrl(), Topics.values());
  }

  @Override
  public void waitForNextConfiguration() throws InterruptedException {
    latch = new CountDownLatch(1);
    latch.await();
  }

  @Override
  public void onMessage(Message msg) {
    TextMessage message = (TextMessage) msg;
    try {
      if (message.getText().equals(uuid)) {
        if (latch != null) {
          latch.countDown();
        }
      }
    } catch (JMSException e) {
      System.err.println("Unable to process message: " + message);
    }
  }

  @Override
  public void close() throws IOException {
    publisher.close();
    closeListener.close();
  }

  @Override
  public void notifyProcessCompletion() throws JMSException {
   publisher.publish(uuid, Topics.PIPELINE_COMPLETE);
  }
}
