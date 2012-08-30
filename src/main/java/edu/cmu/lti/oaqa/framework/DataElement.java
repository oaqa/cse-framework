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

  private final String dataset;

  private final int sequenceId;

  private final String text;
  
  private final String quuid;

  public DataElement(String dataset, int sequenceId, String question, String quuid) {
    this.dataset = dataset;
    this.sequenceId = sequenceId;
    this.text = question;
    this.quuid = quuid;
  }

  public String getDataset() {
    return dataset;
  }

  public int getSequenceId() {
    return sequenceId;
  }

  public String getText() {
    return text;
  }

  public String toString() {
    return "Dataset: " + dataset + " SequenceId: " + sequenceId + " Text: " + text;
  }

  public String getQuuid() {
    return quuid;
  }
}
