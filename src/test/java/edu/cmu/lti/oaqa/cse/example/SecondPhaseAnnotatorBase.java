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
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.framework.CasUtils;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.framework.types.OutputElement;

public class SecondPhaseAnnotatorBase extends JCasAnnotator_ImplBase {
	
	public String getName() { return "SecondPhaseAnnotator"; }

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
	  System.out.printf("process: %s\n", getClass().getSimpleName());
		InputElement input = (InputElement) CasUtils.getFirst(jcas , 
		    "edu.cmu.lti.oaqa.framework.types.InputElement" );
		if ( input == null ) {
		  throw new AnalysisEngineProcessException();
		}
		OutputElement answer = new OutputElement(jcas);
		answer.setAnswer("Vesuvius");
		answer.setSequenceId(input.getSequenceId());
		answer.addToIndexes();
	}

}
