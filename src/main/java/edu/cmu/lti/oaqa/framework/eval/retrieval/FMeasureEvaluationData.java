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

public final class FMeasureEvaluationData {

  final float precision;
  final float recall;

  final float f1;

  final float map;

  final float binaryRecall;

  final int count;

  FMeasureEvaluationData(float precision, float recall, float f1, float docMAP, float binaryRecall,
          int count) {
    this.precision = precision;
    this.recall = recall;
    this.f1 = f1;
    this.map = docMAP;
    this.binaryRecall = binaryRecall;
    this.count = count;
  }

  public float getPrecision() {
    return precision;
  }

  public float getRecall() {
    return recall;
  }

  public float getF1() {
    return f1;
  }

  public float getMap() {
    return map;
  }

  public float getBinaryRecall() {
    return binaryRecall;
  }

  public int getCount() {
    return count;
  }

}