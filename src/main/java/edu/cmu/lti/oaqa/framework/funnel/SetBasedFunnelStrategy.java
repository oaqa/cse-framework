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

package edu.cmu.lti.oaqa.framework.funnel;

import java.util.Set;

import org.apache.uima.analysis_engine.metadata.impl.FixedFlow_impl;

import com.google.common.collect.Sets;

import edu.cmu.lti.oaqa.ecd.flow.FunneledFlow;
import edu.cmu.lti.oaqa.ecd.phase.Trace;

public class SetBasedFunnelStrategy extends FixedFlow_impl implements FunneledFlow {

  private static final long serialVersionUID = -8276851415955188563L;

  private final Set<String> set;

  public SetBasedFunnelStrategy(Iterable<String> traces) {
    this.set = Sets.newHashSet(traces);
  }

  @Override
  public boolean funnel(Trace trace) {
    return set.contains(trace.getTraceHash());
  }
}
