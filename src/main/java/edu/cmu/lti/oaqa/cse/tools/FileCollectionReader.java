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

package edu.cmu.lti.oaqa.cse.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import edu.cmu.lti.oaqa.framework.types.InputElement;

/**
 * File System Collection Reader for questions with oaqa-specific initialization
 * 
 */
public class FileCollectionReader extends CollectionReader_ImplBase {

	public static final String PARAM_QUESTIONFILE = "questionFile";

  public final Pattern pQuestionLine = Pattern
      .compile("^([^\\s]+)\\s+([^\\s].*)$");

	private String dataset;
	
	private BufferedReader reader;
	
	private String line;
	
	private int count = 0;

	@Override
	public void initialize() throws ResourceInitializationException {

    this.dataset = (String) getConfigParameterValue("dataset");
		File file = new File(
				((String) getConfigParameterValue(PARAM_QUESTIONFILE)).trim());
		
		getLogger().log(Level.INFO,"Loading questions from " 
		        + file.getAbsolutePath());

		// if input file does not exist, throw exception
		if (!file.exists()) {
		  getLogger().log(Level.SEVERE,"The question file does not exist.");
			throw new ResourceInitializationException(
					ResourceConfigurationException.DIRECTORY_NOT_FOUND,
					new Object[] { PARAM_QUESTIONFILE,
							this.getMetaData().getName(),
							file.getPath() });
		}

		try {
		this.reader = new BufferedReader(
		        new InputStreamReader(new FileInputStream(file), "UTF8"));
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		} 
	}


  @Override
  public boolean hasNext() {
    try {
      return (line = reader.readLine()) != null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
	
  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    try {
      JCas jcas = aCAS.getJCas();
      line = line.trim();
      if (line.length() == 0 || line.matches("^\\s*#.+")) {
        return;
      }

      Matcher mQuestionLine = pQuestionLine.matcher(line);

      if (mQuestionLine.find()) {
        String questionId = mQuestionLine.group(1);
        String questionText = mQuestionLine.group(2);

        // Annotate as input element
        InputElement next = new InputElement(jcas);
        next.setDataset(dataset);
        next.setQuestion(questionText);
        next.setAnswerPattern("");
        next.setSequenceId(Integer.parseInt(questionId));
        next.addToIndexes();
      }
      count++;
    } catch (CASException e) {
      throw new CollectionException(e);
    }
  }

  @Override
	public void close() throws IOException {
     reader.close();
	}

  @Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(count, -1, Progress.ENTITIES) };
	}

}
