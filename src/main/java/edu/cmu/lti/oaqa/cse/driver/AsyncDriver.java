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

import java.util.UUID;

import mx.bigdata.anyobject.AnyObject;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Progress;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.ExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.config.ConfigurationLoader;
import edu.cmu.lti.oaqa.ecd.config.Stage;
import edu.cmu.lti.oaqa.ecd.config.StagedConfiguration;
import edu.cmu.lti.oaqa.ecd.config.StagedConfigurationImpl;
import edu.cmu.lti.oaqa.ecd.driver.SimplePipelineRev803;
import edu.cmu.lti.oaqa.ecd.driver.strategy.DefaultProcessingStrategy;
import edu.cmu.lti.oaqa.ecd.driver.strategy.ProcessingStrategy;
import edu.cmu.lti.oaqa.ecd.flow.FunneledFlow;
import edu.cmu.lti.oaqa.framework.async.ConsumerManager;
import edu.cmu.lti.oaqa.framework.async.ConsumerManagerImpl;
import edu.cmu.lti.oaqa.framework.async.ProducerManager;
import edu.cmu.lti.oaqa.framework.async.ProducerManagerImpl;

public final class AsyncDriver {

  private final ExperimentBuilder builder;

  private final AnyObject config;

  private final OpMode opMode;

  private final AsyncConfiguration asyncConfig;

  private final AnyObject localConfig;

  public AsyncDriver(String resource, String uuid, OpMode op) throws Exception {
    this.opMode = op;
    this.localConfig = ConfigurationLoader.load(resource);
    if (opMode == OpMode.PRODUCER) {
      resource += "-producer";
      this.config = ConfigurationLoader.load(resource);
    } else {
      resource += "-consumer";
      this.config = ConfigurationLoader.load(resource);
    }
    TypeSystemDescription typeSystem = TypeSystemDescriptionFactory.createTypeSystemDescription();
    this.builder = new BaseExperimentBuilder(uuid, resource, typeSystem);
    this.asyncConfig = builder.initializeResource(config, "async-configuration", AsyncConfiguration.class);
  }

  public void run() throws Exception {
    if (opMode == OpMode.PRODUCER) {
      runProducer();
    } else {
      runConsumer();
    }
  }

  private ProcessingStrategy getProcessingStrategy() throws ResourceInitializationException {
    ProcessingStrategy ps = new DefaultProcessingStrategy();
    AnyObject map = config.getAnyObject("processing-strategy");
    if (map != null) {
      ps = BaseExperimentBuilder.loadProvider(map, ProcessingStrategy.class);
    }
    return ps;
  }

  private void runProducer() throws Exception {
    StagedConfiguration stagedConfig = new StagedConfigurationImpl(config);
    ProducerManager manager = new ProducerManagerImpl(builder.getExperimentUuid(), asyncConfig);
    try {
      for (Stage stage : stagedConfig) {
        AnyObject conf = stage.getConfiguration();
        CollectionReader reader = builder.buildCollectionReader(conf, stage.getId());
        AnalysisEngine noOp = builder.createNoOpEngine();
        SimplePipelineRev803.runPipeline(reader, noOp);
        Progress progress = reader.getProgress()[0];
        long total = progress.getCompleted();
        manager.waitForReaderCompletion(total);
        manager.notifyCloseCollectionReaders();
        manager.waitForProcessCompletion();
        // TODO: This is bogus USE a local reader for evaluation and funneling!!!
        CollectionReader postReader = builder.buildCollectionReader(localConfig, stage.getId());
        AnalysisEngine post = builder.buildPipeline(stage.getConfiguration(), "post-process",
                stage.getId());
        SimplePipelineRev803.runPipeline(postReader, post);
        manager.notifyNextConfigurationIsReady();
      }
    } finally {
      manager.close();
    }
  }

  private void runConsumer() throws Exception {
    StagedConfiguration stagedConfig = new StagedConfigurationImpl(config);
    ConsumerManager manager = new ConsumerManagerImpl(builder.getExperimentUuid(), asyncConfig);
    ProcessingStrategy ps = getProcessingStrategy();
    try {
      for (Stage stage : stagedConfig) {
        try {
          FunneledFlow funnel = ps.newFunnelStrategy(builder.getExperimentUuid());
          CollectionReader reader = builder.buildCollectionReader(stage.getConfiguration(),
                  stage.getId());
          AnalysisEngine pipeline = builder.buildPipeline(stage.getConfiguration(), "pipeline",
                  stage.getId(), funnel);
          SimplePipelineRev803.runPipeline(reader, pipeline);
          manager.notifyProcessCompletion();
          manager.waitForNextConfiguration();
        } catch (UIMAException e) {
          e.printStackTrace();
        }
      }
    } finally {
      manager.close();
    }
  }

  public static void main(String[] args) throws Exception {
    OpMode op = OpMode.valueOf(args[1]);
    String uuid = UUID.randomUUID().toString();
    if (args.length > 2) {
      uuid = args[2];
    }
    System.out.println("Experiment UUID: " + uuid);
    AsyncDriver driver = new AsyncDriver(args[0], uuid, op);
    driver.run();
  }
}