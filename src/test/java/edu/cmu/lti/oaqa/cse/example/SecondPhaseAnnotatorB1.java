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

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.oaqa.model.test.Passage;
import org.oaqa.model.test.Search;

import edu.cmu.lti.oaqa.ecd.log.LogEntry;
import edu.cmu.lti.oaqa.framework.ViewManager;

public class SecondPhaseAnnotatorB1 extends JCasAnnotator_ImplBase {
  
  enum ExampleLogEntry implements LogEntry {TOP_MATCHES};
  
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    System.out.printf("process: %s\n", getClass().getSimpleName());
    try {
      JCas documentView = ViewManager.getDocumentView(jcas);
      FSArray hitList = new FSArray(documentView, 7);
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < 7; i++) {
        Passage sr = new Passage(documentView);
        sr.addToIndexes();
        sr.setRank((i + 1));
        sr.setText("Dummy answer " + i);
        builder.append("Dummy answer " + i);
        builder.append("\n");
        sr.setScore(i);
        sr.setUri(String.valueOf(i + 1));
        sr.setQueryString("Dummy query string " + i);
        hitList.set(i, sr);
      }

      Search search = new Search(documentView);
      search.setHitList(hitList);
      search.addToIndexes();
      
    } catch (CASException e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

}