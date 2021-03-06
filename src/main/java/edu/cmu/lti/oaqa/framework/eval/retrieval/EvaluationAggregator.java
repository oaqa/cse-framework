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

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.Resource;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import edu.cmu.lti.oaqa.framework.eval.Key;

public interface EvaluationAggregator<T> extends Resource {

  void update(Key key, String sequenceId, List<T> docs, List<T> goldStandard, Ordering<T> ordering, Function<T, String> toIdString) throws AnalysisEngineProcessException;

}
  