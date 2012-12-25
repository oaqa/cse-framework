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

package edu.cmu.lti.oaqa.framework.collection.fs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import mx.bigdata.streaming.RecordBuilder;
import mx.bigdata.streaming.StreamingRecordReader;

import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.collection.IterableCollectionReader;

public final class FileCollectionReader extends IterableCollectionReader {

  private static final String FILE_PROPERTY = "openqa.collection.filename";

  private StreamingRecordReader<DataElement> reader;

  @Override
  protected Iterator<DataElement> getInputSet() throws ResourceInitializationException {
    String filename = System.getProperty(FILE_PROPERTY);
    try {
      if (filename == null) {
        this.reader = buildIteratorFromFile();
      } else {
        InputStream in = new FileInputStream(filename);
        this.reader = buildIterator(in);
      }
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    return reader.iterator();
  }

  private StreamingRecordReader<DataElement> buildIteratorFromFile() throws IOException {
    System.err.printf("%s system property not specified, using 'file'" 
            + " parameter from configuration file\n",FILE_PROPERTY);
    String resource = (String) getConfigParameterValue("file");
    if (resource != null) {
      System.err.printf("Reading file: %s from the classpath\n", resource);
      InputStream in = getClass().getResourceAsStream(resource);
      return buildIterator(in);
    } else {
      throw new IllegalArgumentException(String.format("Parameter 'file' must be specified"));
    }
  }

  private StreamingRecordReader<DataElement> buildIterator(InputStream in) throws IOException {
    Reader reader = new InputStreamReader(in);
    return StreamingRecordReader.newReader(reader, new DataElementBuilder());
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  private final class DataElementBuilder implements RecordBuilder<DataElement> {
    @Override
    public DataElement build(String line) {
      String[] data = line.split("\\|");
      return new DataElement(getDataset(), data[0], data[1], null);
    }
  }
}
