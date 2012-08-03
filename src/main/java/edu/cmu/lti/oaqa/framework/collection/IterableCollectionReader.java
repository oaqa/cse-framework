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
import java.util.Iterator;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.framework.DataElement;

public abstract class IterableCollectionReader extends AbstractCollectionReader {

  private Iterator<DataElement> inputs;

  @Override
  public void initialize() throws ResourceInitializationException {
    super.initialize();
    this.inputs = getInputSet();
  }

  protected abstract Iterator<DataElement> getInputSet() throws ResourceInitializationException;

  protected DataElement getNextElement() throws Exception {
    return inputs.next();
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return inputs.hasNext();
  }
}