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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.resource.ResourceInitializationException;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.collection.IterableCollectionReader;

public final class DirCollectionReader extends IterableCollectionReader {

  private static final String DIR_PROPERTY = "openqa.collection.dir";

  @Override
  protected Iterator<DataElement> getInputSet() throws ResourceInitializationException {
    String filename = System.getProperty(DIR_PROPERTY);
    try {
      if (filename == null) {
        Iterator<Resource> files = getFilesFromParams();
        return iterator(files);
      } else {
        File file = new File(filename);
        String[] files = file.list();
        return iterator(Iterators.transform(Iterators.forArray(files), 
                new StringToResourceFunction("")));
      }
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  private Iterator<DataElement> iterator(final Iterator<Resource> resources) {
    return new Iterator<DataElement>() {

      @Override
      public boolean hasNext() {
        return resources.hasNext();
      }

      @Override
      public DataElement next() {
        Resource resource = resources.next();
        try {
          String content = resource.getContent();
          String id = resource.getId();
          return new DataElement(getDataset(), id, content, null);
        } catch (IOException e) {
          throw Throwables.propagate(e);
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

    };
  }

  private Iterator<Resource> getFilesFromParams() throws IOException {
    System.err.printf("%s system property not specified, using 'dir'"
            + " parameter from configuration file\n", DIR_PROPERTY);
    String resource = (String) getConfigParameterValue("dir");
    String suffix = (String) getConfigParameterValue("suffix");
    if (resource != null) {
      System.err.printf("Reading files from classpath directory: %s\n", resource);
      Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(
              ClasspathHelper.forPackage("")).setScanners(new ResourcesScanner()));
      Set<String> files = reflections.getResources(Pattern.compile(".*\\." + suffix));
      Collection<Resource> resources = Collections2.transform(files, new StringToResourceFunction("/"));
      final Pattern p = Pattern.compile("^" + resource);
      Collection<Resource> filtered = Collections2.filter(resources, new Predicate<Resource>() {
        @Override
        public boolean apply(Resource input) {
          Matcher m = p.matcher(input.name);
          return m.find();
        }
      });
      return filtered.iterator();
    } else {
      throw new IOException(String.format("Parameter 'dir' must be specified"));
    }
  }
  
  @Override
  public void close() throws IOException {
  }
  
  private final class StringToResourceFunction implements Function<String, Resource> {
   
    private final String prefix;
    
    private StringToResourceFunction(String prefix) {
      this.prefix = prefix;
    }
    
    @Override
    public Resource apply(String input) {
      return new Resource(prefix + input, Type.CLASSPATH);
    }
  }
  
  private enum Type {FILE, CLASSPATH}
  
  private final class Resource {
    final String name;
    final Type type;
    
    public Resource(String name, Type type) {
      this.name = name;
      this.type = type;
    }
    
    public String getContent() throws IOException {
      if (type == Type.FILE) {
        return Files.toString(new File(name), Charsets.UTF_8);
      } else {
        URL url = getClass().getResource(name);
        return Resources.toString(url, Charsets.UTF_8);
      }
    }

    public String getId() {
      return name;
    }
  }
}
