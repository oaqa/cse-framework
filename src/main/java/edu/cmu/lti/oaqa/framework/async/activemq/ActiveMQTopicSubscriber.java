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

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQTopicSubscriber implements ExceptionListener, Closeable {

  private Connection connection;

  public ActiveMQTopicSubscriber(String url, MessageListener listener, Topic subject) throws JMSException {
    String user = ActiveMQConnection.DEFAULT_USER;
    String password = ActiveMQConnection.DEFAULT_PASSWORD;
    System.out.println("Attempting connection to: " + url);
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
    this.connection = connectionFactory.createConnection();
    connection.setExceptionListener(this);
    connection.start();
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Destination destination = session.createTopic(subject.toString());
    MessageConsumer consumer = session.createConsumer(destination);
    consumer.setMessageListener(listener);
  }

  @Override
  public void close() throws IOException {
    try {
      connection.close();
    } catch (Exception e) {
      throw new IOException(e);
    }
  }
  
  @Override
  public void onException(JMSException e) {
    e.printStackTrace();
  }

}
