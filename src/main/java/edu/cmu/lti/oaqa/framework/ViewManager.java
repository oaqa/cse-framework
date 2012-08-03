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

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

//Responsible for creating and accessing JCAS views safely... in HelloQA, only candidateView and finalAnswerView are used. 

/**
 * 
 * @author dadamson
 *
 */
public class ViewManager {

  public interface Type {}
  
  public enum ViewType implements Type {QUESTION, DOCUMENT, CANDIDATE, FINAL_ANSWER, 
    QUESTION_GS, DOCUMENT_GS, CANDIDATE_GS, FINAL_ANSWER_GS}

  public static JCas getQuestionView(JCas jcas) throws CASException
  {
    return getOrCreateView(jcas, ViewType.QUESTION);
  }

  public static JCas getDocumentView(JCas jcas) throws CASException
  {
    return getOrCreateView(jcas, ViewType.DOCUMENT);
  }

  public static JCas getCandidateView(JCas jcas) throws CASException
  {
    return getOrCreateView(jcas, ViewType.CANDIDATE);
  }

  public static JCas getFinalAnswerView(JCas jcas) throws CASException
  {
    return getOrCreateView(jcas, ViewType.FINAL_ANSWER);
  }
  
  public static JCas getView(JCas jcas, Type type) throws CASException {
    String viewName = type.toString();
    try {
      return jcas.getView(viewName);
    } catch (Exception e) {
      return null;
    }
  }
  
  public static JCas getOrCreateView(JCas jcas, Type type) throws CASException {
    String viewName = type.toString();
    try {
      return jcas.getView(viewName);
    } catch (Exception e) {
      jcas.createView(viewName);
      return jcas.getView(viewName);
    }
  }
  
}


