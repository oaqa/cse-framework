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

package edu.cmu.lti.oaqa.framework.eval;

import com.google.common.base.Objects;

public final class ExperimentKey {
  private final String experiment;

  private final int stage;

  public ExperimentKey(String experiment, int stage) {
    this.experiment = experiment;
    this.stage = stage;
  }

  public int hashCode() {
    return Objects.hashCode(getExperiment(), getStage());
  }

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof ExperimentKey))
      return false;
    ExperimentKey k = (ExperimentKey) o;
    return k.getStage()== getStage() && k.getExperiment().equals(getExperiment());
  }

  public int getStage() {
    return stage;
  }

  public String toString() {
    return getExperiment() + ":" + getStage();
  }

  public String getExperiment() {
    return experiment;
  }
}