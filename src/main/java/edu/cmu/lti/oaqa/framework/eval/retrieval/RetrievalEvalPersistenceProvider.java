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

package edu.cmu.lti.oaqa.framework.eval.retrieval;

import java.sql.SQLException;

import org.apache.uima.resource.Resource;

import com.google.common.collect.Multimap;

import edu.cmu.lti.oaqa.framework.eval.ExperimentKey;
import edu.cmu.lti.oaqa.framework.eval.Key;

public interface RetrievalEvalPersistenceProvider extends Resource {
  void insertPartialCounts(Key key, String sequenceId, RetrievalCounts counts) throws Exception;

  void deletePassageAggrEval(Key key, String sequenceId);
  
  void deleteFMeasureEval(ExperimentKey experiment);
  
  void insertFMeasureEval(Key key, String eName, FMeasureEvaluationData eval) throws SQLException; 
  
  Multimap<Key, RetrievalCounts> retrievePartialCounts(ExperimentKey experiment);
}
