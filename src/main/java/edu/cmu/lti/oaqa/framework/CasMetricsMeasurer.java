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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.cse.driver.CasUtils;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.framework.types.OutputElement;

@Deprecated
public class CasMetricsMeasurer extends JCasAnnotator_ImplBase {
	private final String inputTypeName = "edu.cmu.lti.oaqa.framework.types.InputElement";
	private final String outputTypeName = "edu.cmu.lti.oaqa.framework.types.OutputElement";
	private Map <String,Integer> totalCases;
	private Map <String,Integer> totalCorrect;
	private PrintStream out;
	
	public void initialize( UimaContext c ) {
		System.out.println( "CasMetricsMeasurer.initialize()" );
		totalCases = new HashMap<String,Integer>();
		totalCorrect = new HashMap<String,Integer>();
		String outputFilename = (String) c.getConfigParameterValue("outputFile");
		if (outputFilename == null) {
		  throw new RuntimeException("outputFile is null");
		}
		try {
			out = new PrintStream(new FileOutputStream(outputFilename));
			out.println( "Configuration" + "|" +
					"Total CASes" + "|" + 
					"Total Correct" + "|" + 
					"Precision" );
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void process( JCas jcas ) throws AnalysisEngineProcessException {
		String configId = System.getProperty( "CasConsumer.configId" );
		if ( configId == null ) {
		  throw new AnalysisEngineProcessException( new RuntimeException( "configId is null " ));
		}
		Integer total = totalCases.get( configId );
		if ( total == null ) {  
		  total = new Integer( 1 ); totalCases.put( configId , total );
		}	else { 
		  totalCases.put( configId , new Integer( total + 1 ) ); 
		}
		InputElement input = (InputElement)CasUtils.getFirst( jcas , inputTypeName );
		if ( input == null ) {
		  throw new AnalysisEngineProcessException( new NullPointerException( "Missing InputElement" ));
		}
		OutputElement output = (OutputElement)CasUtils.getFirst( jcas , outputTypeName );
		if ( output == null ) {
		  throw new AnalysisEngineProcessException( new NullPointerException( "Missing OutputElement" ));
		}
		Integer correct = totalCorrect.get( configId );
		if ( correct == null ) { 
		  correct = new Integer( 0 ); totalCorrect.put( configId ,  correct ); 
		  }
		if ( output.getAnswer().equals( input.getAnswerPattern() )) {
			totalCorrect.put( configId , new Integer( correct + 1 ) );
		}
	}
	
	public void collectionProcessComplete() {
		System.out.println( "CasMetricsMeasurer.collectionProcessComplete()" );
		for ( String key : totalCases.keySet() ) {
			System.out.println( "Configuration: " + key );
			double total = totalCases.get( key );
			double correct = totalCorrect.get( key );
			System.out.println( "  total: " + total );
			System.out.println( "  correct: " + correct );
			double precision = 0.0d;
			if (total > 0) {
				precision = correct / total;
			}
			System.out.println( "  precision: " + precision );
			out.println( key + "|" + totalCases.get( key ) + "|" + totalCorrect.get( key ) + "|" + precision );
		}
		out.close();
	}


}
