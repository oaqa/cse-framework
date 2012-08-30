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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Test;
import org.oaqa.model.test.Passage;
import org.oaqa.model.test.Search;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import com.google.common.collect.Lists;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.ExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.phase.ProcessingStepUtils;
import edu.cmu.lti.oaqa.framework.DataElement;
import edu.cmu.lti.oaqa.framework.ViewManager;
import edu.cmu.lti.oaqa.framework.collection.adhoc.AdHocCollectionReader;
import edu.cmu.lti.oaqa.framework.collection.adhoc.AdHocSource;
import edu.cmu.lti.oaqa.framework.collection.adhoc.BaseAdHocSource;
import edu.cmu.lti.oaqa.framework.collection.adhoc.SyncCasProcessedCallbackListener;
import edu.cmu.lti.oaqa.framework.types.InputElement;

public class AdHocDriverTest {

  @Test
  public void setupAdhocPipeline() throws Exception {
    String uuid = UUID.randomUUID().toString();
    TypeSystemDescription typeSystem = TypeSystemDescriptionFactory.createTypeSystemDescription();
    ExperimentBuilder builder = new BaseExperimentBuilder(uuid, "test.adhoc-cse-example",  typeSystem);
    AdHocDriver driver = new AdHocDriver(builder);
    AdHocSource source = new BaseAdHocSource() {
      public void publish(String quuid, String question) throws InterruptedException {
        DataElement result = new DataElement("AdHoc Dataset", 100, question, quuid);
        getReader().putQuestion(result);
      }
    };
    ResultSizeCallbackListener callback = new ResultSizeCallbackListener();
    AdHocCollectionReader reader = driver.setupAndRun(source, callback);
    String quuid = UUID.randomUUID().toString();
    source.publish(quuid, "This is an empty question");
    callback.await();
    assertThat(callback.size, is(equalTo(7)));
    assertThat(callback.quuid, is(equalTo(quuid)));
    reader.shutdown();
  }

  private static final class ResultSizeCallbackListener extends SyncCasProcessedCallbackListener {
 
    int size; 
    
    String quuid;
    
    @Override
    public void entityProcessComplete(CAS aCas) {
      try {
        JCas jcas = aCas.getJCas();
        JCas view = ViewManager.getDocumentView(jcas);
        List<Passage> docs = (view != null) ? loadDocumentSet(view)
                : Collections.<Passage> emptyList();
        size = docs.size();
        InputElement input = ProcessingStepUtils.getInputElement(jcas);
        this.quuid = input.getQuuid();
        latch.countDown();
      } catch (CASException e) {
        e.printStackTrace();
      }
    }
    
    public static List<Passage> loadDocumentSet(JCas jcas) {
      List<Passage> result = Lists.newArrayList();
      Iterator<?> it = jcas.getJFSIndexRepository().getAllIndexedFS(Search.type);
      if (it.hasNext()) {
        Search retrievalResult = (Search) it.next();
        FSArray hitList = retrievalResult.getHitList();
        for (int i = 0; i < hitList.size(); i++) {
          Passage sr = (Passage) hitList.get(i);
          result.add(sr);
        }
      }
      return result;
    }
  }
}
