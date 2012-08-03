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

package edu.cmu.lti.oaqa.cse.driver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mx.bigdata.anyobject.AnyObject;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.CasIterator;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.util.CasCreationUtils;

import edu.cmu.lti.oaqa.ecd.ExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.driver.SimplePipelineRev803;
import edu.cmu.lti.oaqa.framework.collection.adhoc.AdHocCollectionReader;
import edu.cmu.lti.oaqa.framework.collection.adhoc.AdHocSource;
import edu.cmu.lti.oaqa.framework.collection.adhoc.CasProcessedCallback;

public final class AdHocDriver {

  private final ExperimentBuilder builder;

  private final AnyObject config;

  public AdHocDriver(ExperimentBuilder builder) throws Exception {
    this.config = builder.getConfiguration();
    this.builder = builder;
    System.out.println("Experiment UUID: " + builder.getExperimentUuid());
  }

  AdHocCollectionReader setupAndRunAdHoc(AdHocSource source, final CasProcessedCallback callback,
          boolean execute) throws Exception, UIMAException, IOException {
    final AdHocCollectionReader reader = (AdHocCollectionReader) builder.buildCollectionReader(config, 0);
    source.setCollectionReader(reader);
    final AnalysisEngine pipeline = builder.buildPipeline(config, "pipeline", 1, null, true);
    if (execute) {
      new Thread(new Runnable() {
        public void run() {
          try {
            runPipeline(reader, pipeline, callback);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }).start();
    }
    return reader;
  }

  public AdHocCollectionReader setupAndRun(AdHocSource source, CasProcessedCallback callback)
          throws Exception, UIMAException, IOException {
    return setupAndRunAdHoc(source, callback, true);
  }

  void runPipeline(final CollectionReader reader, AnalysisEngine engine,
          CasProcessedCallback callback) throws UIMAException, IOException {
    final List<ResourceMetaData> metaData = new ArrayList<ResourceMetaData>();
    metaData.add(reader.getMetaData());
    metaData.add(engine.getMetaData());
    final CAS cas = CasCreationUtils.createCas(metaData);
    try {
      while (reader.hasNext()) {
        reader.getNext(cas);
        runPipeline(cas, engine, callback);
        cas.reset();
      }
    } finally {
      SimplePipelineRev803.collectionProcessComplete(engine);
      SimplePipelineRev803.destroy(reader);
    }
  }

  void runPipeline(final CAS cas, final AnalysisEngine engine, CasProcessedCallback callback)
          throws UIMAException, IOException {
    CasIterator it = engine.processAndOutputNewCASes(cas);
    while (it.hasNext()) {
      CAS output = it.next();
      callback.entityProcessComplete(output);
      output.release();
    }
  }
}