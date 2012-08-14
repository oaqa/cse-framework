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

package edu.cmu.lti.oaqa.framework.report;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.CasConsumer_ImplBase;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.phase.ProcessingStepUtils;
import edu.cmu.lti.oaqa.framework.eval.ExperimentKey;
import edu.cmu.lti.oaqa.framework.types.ExperimentUUID;

public class CsvReportGeneratorConsumer extends CasConsumer_ImplBase implements ReportGenerator {

  private List<ReportComponentBuilder> builders;

  private Set<ExperimentKey> experiments = Sets.newHashSet();

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    Object builderNames = (Object) context.getConfigParameterValue("builders");
    if (builderNames != null) {
      this.builders = BaseExperimentBuilder.createResourceList(builderNames, 
              ReportComponentBuilder.class);
    }
  }

  @Override
  public void process(CAS aCAS) throws AnalysisEngineProcessException {
    try {
      JCas jcas = aCAS.getJCas();
      ExperimentUUID experiment = ProcessingStepUtils.getCurrentExperiment(jcas);
      experiments.add(new ExperimentKey(experiment.getUuid(), experiment.getStageId()));
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

  @Override
  public void collectionProcessComplete() throws AnalysisEngineProcessException {
    System.out.println(" ------------------ EVALUATION REPORT ------------------");
    for (ExperimentKey experiment : experiments) {
      System.out.println("Experiment: " + experiment);
      for (ReportComponentBuilder builder : builders) {
        ReportComponent rc = builder.getReportComponent(experiment.getExperiment(),
                String.valueOf(experiment.getStage()));
        System.out.println(print(rc));
      }
    }
    System.out.println(" -------------------------------------------------------");
  }

  private String print(ReportComponent rc) {
    Joiner joiner = Joiner.on(",");
    StringBuilder sb = new StringBuilder();
    List<String> headers = rc.getHeaders();
    joiner.appendTo(sb, headers);
    sb.append("\n");
    Table<String, String, String> table = rc.getTable();
    for (Map<String, String> row : table.rowMap().values()) {
      List<String> values = Lists.newArrayList();
      for (String header : headers) {
        values.add(escape(row.get(header)));
      }
      joiner.appendTo(sb, values);
      sb.append("\n");
    }
    return sb.toString();
  }

  private String escape(String string) {
    return string.replace(",", "_");
  }
}
