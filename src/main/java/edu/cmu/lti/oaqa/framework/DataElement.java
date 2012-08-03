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

package edu.cmu.lti.oaqa.framework;

public class DataElement {
  private final int id;

  private final String dataset;

  private final int sequenceId;

  private final String question, answerPattern;
  
  private final String quuid;

  public DataElement(int id, String dataset, int sequenceId, String question, String answerPattern, String quuid) {
    this.id = id;
    this.dataset = dataset;
    this.sequenceId = sequenceId;
    this.question = question;
    this.answerPattern = answerPattern;
    this.quuid = quuid;
  }

  public int getId() {
    return id;
  }

  public String getDataset() {
    return dataset;
  }

  public int getSequenceId() {
    return sequenceId;
  }

  public String getQuestion() {
    return question;
  }

  public String getAnswerPattern() {
    return answerPattern;
  }

  public String toString() {
    return "Dataset: " + dataset + " SequenceId: " + sequenceId + " Question: " + question
            + " AnswerPattern: " + answerPattern;
  }

  public String getQuuid() {
    return quuid;
  }
}
