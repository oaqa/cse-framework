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

package edu.cmu.lti.oaqa.framework.persistence;

import java.sql.SQLException;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import edu.cmu.lti.oaqa.framework.eval.ExperimentKey;
import edu.cmu.lti.oaqa.framework.eval.Key;
import edu.cmu.lti.oaqa.framework.eval.retrieval.FMeasureEvaluationData;
import edu.cmu.lti.oaqa.framework.eval.retrieval.RetrievalCounts;

public class DefaultRetrievalEvalPersistenceProvider extends
        AbstractRetrievalEvalPersistenceProvider {

  @Override
  public void insertPartialCounts(Key key, int sequenceId, RetrievalCounts counts) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public void deletePassageAggrEval(Key key, int sequenceId) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteFMeasureEval(ExperimentKey experiment) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void insertFMeasureEval(Key key, String eName, FMeasureEvaluationData eval)
          throws SQLException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Multimap<Key, RetrievalCounts> retrievePartialCounts(ExperimentKey experiment) {
    // TODO Auto-generated method stub
    return LinkedHashMultimap.create();
  }

}
