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

package edu.cmu.lti.oaqa.cse.example;

import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.Resource_ImplBase;

import edu.cmu.lti.oaqa.ecd.eval.Key;
import edu.cmu.lti.oaqa.ecd.eval.TraceListener;

public class ExampleTraceListener extends Resource_ImplBase implements TraceListener {

  @Override
  public boolean initialize(ResourceSpecifier aSpecifier,
          Map<String,Object> aAdditionalParams) throws ResourceInitializationException {
    String foo = (String) aAdditionalParams.get("foo");
    System.out.println(getClass().getSimpleName() + "> foo:" + foo);
    return true;
  }

  @Override
  public void process(Key key, JCas jcas) {
    System.out.printf("Trace %s: %s\n", key.hashString(), key.getTrace());
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    
  }
}
