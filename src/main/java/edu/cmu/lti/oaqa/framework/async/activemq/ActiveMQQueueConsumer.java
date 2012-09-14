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
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQQueueConsumer implements Closeable {

  private Connection connection;

  private MessageConsumer consumer;
  
  private Session session;

  public ActiveMQQueueConsumer(String url, String queue) throws JMSException {
    String user = ActiveMQConnection.DEFAULT_USER;
    String password = ActiveMQConnection.DEFAULT_PASSWORD;
    System.out.println("Attempting connection to: " + url);
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
    this.connection = connectionFactory.createConnection();
    connection.start();
    this.session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
    Destination destination = session.createQueue(queue);
    this.consumer = session.createConsumer(destination);
  }

  public Message receive() throws JMSException {
    return consumer.receive();
  }

  public Message receive(long timeout) throws JMSException {
    return consumer.receive(timeout);
  }
  
  public void recover() {
    try {
      session.recover();
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void close() throws IOException {
    try {
      consumer.close();
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
