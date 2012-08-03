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

package edu.cmu.lti.oaqa.framework.collection.adhoc;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

import org.apache.uima.collection.CollectionException;

import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.collection.AbstractCollectionReader;

public final class AdHocCollectionReader extends AbstractCollectionReader {

  private final SynchronousQueue<DataElement> queue = new SynchronousQueue<DataElement>();
  
  private boolean processing = true;

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return processing;
  }
  
  protected DataElement getNextElement() throws Exception {
    return queue.take();
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub
  }
  
  /**
   * Adds the specified element to this collection reader, 
   * waiting if necessary for the reader to be able to process it.
   * @param result
   * @throws InterruptedException
   */
  public void putQuestion(DataElement result) throws InterruptedException {
    queue.put(result);
  }

  public void shutdown() {
    this.processing = false;
  } 
}
