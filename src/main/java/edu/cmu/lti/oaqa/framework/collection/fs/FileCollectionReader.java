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
        System.err.printf("%s property not specified, using 'file' parameter from configuration\n",
                FILE_PROPERTY);
        String resource = (String) getConfigParameterValue("file");
        InputStream in = getClass().getResourceAsStream(resource);
        this.reader = buildIterator(in);
      } else {
        InputStream in = new FileInputStream(filename);
        this.reader = buildIterator(in);
      }
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    return reader.iterator();
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
      return new DataElement(getDataset(), Integer.parseInt(data[0]), data[1], null);
    }
  }
}
