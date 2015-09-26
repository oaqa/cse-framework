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
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import edu.cmu.lti.oaqa.ecd.phase.Trace;

import java.nio.charset.StandardCharsets;

public final class Key {
  private final String experiment;

  private final Trace trace;

  private final int stage;

  public Key(String experiment, Trace trace, int stage) {
    this.experiment = experiment;
    this.stage = stage;
    this.trace = trace;
  }

  public int hashCode() {
    return Objects.hashCode(getExperiment(), getTrace());
  }

  public String hashString() {
    HashFunction hf = Hashing.sha256();
    Hasher hasher = hf.newHasher();
    hasher.putString(getExperiment(), StandardCharsets.UTF_16LE);
    hasher.putString(getTrace().getTrace(), StandardCharsets.UTF_16LE);
    HashCode hash = hasher.hash();
    return hash.toString();
  }

  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Key))
      return false;
    Key k = (Key) o;
    return k.getTrace().equals(getTrace()) && k.getExperiment().equals(getExperiment());
  }

  public int getStage() {
    return stage;
  }

  public String toString() {
    return getExperiment() + "," + getTrace() + ": " + getStage();
  }

  public String getExperiment() {
    return experiment;
  }

  public Trace getTrace() {
    return trace;
  }
}