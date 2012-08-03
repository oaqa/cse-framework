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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class ThirdPhaseAnnotatorA1 extends JCasAnnotator_ImplBase {

  private String pa;
  private String pb;
  private String pc;
  private String pd;
  private String foo;
  
  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    this.foo = (String) context.getConfigParameterValue("foo");
    if (!foo.equals("bar")) {
      throw new ResourceInitializationException(
              new RuntimeException("Expected foo == bar"));
    }
    this.pa = (String) context.getConfigParameterValue("parameter-a");
    if (pa == null) {
      throw new ResourceInitializationException(
              new RuntimeException("Expected parameter-a != null"));
    }
    this.pb = (String) context.getConfigParameterValue("parameter-b");
    if (pb == null) {
      throw new ResourceInitializationException(
              new RuntimeException("Expected parameter-b != null"));
    }
    this.pc = (String) context.getConfigParameterValue("parameter-c");
    if (pc == null) {
      throw new ResourceInitializationException(
              new RuntimeException("Expected parameter-c != null"));
    }
    this.pd = (String) context.getConfigParameterValue("parameter-d");
    if (pd == null) {
      throw new ResourceInitializationException(
              new RuntimeException("Expected parameter-d != null"));
    }
  }
	
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    System.out.printf("process: %s(%s,%s,%s,%s,%s)\n", getClass().getSimpleName(),
            foo,pa,pb,pc,pd);
  }
}
