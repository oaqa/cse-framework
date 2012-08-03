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

package edu.cmu.lti.oaqa.framework.async.activemq;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.google.common.collect.Maps;

public class ActiveMQTopicPublisher implements Closeable {

  private Connection connection;

  private Map<Topic, MessageProducer> publishers = Maps.newHashMap();

  private Session session;

  public ActiveMQTopicPublisher(String url, Topic... subjects) throws JMSException {
    String user = ActiveMQConnection.DEFAULT_USER;
    String password = ActiveMQConnection.DEFAULT_PASSWORD;
    System.out.println("Attempting connection to: " + url);
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
    this.connection = connectionFactory.createConnection();
    connection.start();
    this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    for (Topic subject : subjects) {
      Destination destination = session.createTopic(subject.toString());
      MessageProducer publisher = session.createProducer(destination);
      publishers.put(subject, publisher);
    }
  }

  public void publish(String msg, Topic topic) throws JMSException {
    MessageProducer publisher = publishers.get(topic);
    TextMessage message = session.createTextMessage(msg);
    publisher.send(message);
  }

  @Override
  public void close() throws IOException {
    for (MessageProducer publisher : publishers.values()) {
      try {
        publisher.close();
      } catch (Exception e) {
        // Ignore
      }
    }
    try {
      session.close();
    } catch (Exception e) {
      // Ignore
    }
    try {
      connection.close();
    } catch (Exception e) {
      // Ignore
    }
  }
}
