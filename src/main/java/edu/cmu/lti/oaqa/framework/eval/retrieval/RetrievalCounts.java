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

public final class RetrievalCounts {
	private int relevantRetrieved;

	private int retrieved;

	private int relevant;
  
	private float avep;

	private int binaryRelevant;
	
	private int count;
	
	public RetrievalCounts() {
		this(0, 0, 0, 0, 0, 0);
	}

	public RetrievalCounts(int relevantRetrieved, int retrieved, int relevant, float avep, int binaryRelevant, int num) {
		this.relevantRetrieved = relevantRetrieved;
		this.retrieved = retrieved;
		this.relevant = relevant;
		this.avep = avep;
		this.count = num;
		this.binaryRelevant = binaryRelevant;
	}

	void update(RetrievalCounts other) {
		relevantRetrieved += other.relevantRetrieved;
		retrieved += other.retrieved;
		relevant += other.relevant;
		avep += other.avep;
		count += other.count;
		binaryRelevant |= other.binaryRelevant;
	}

  public int getRelevantRetrieved() {
    return relevantRetrieved;
  }

  public int getRetrieved() {
    return retrieved;
  }
  
  public float getAvep() {
    return avep;
  }

  public int getBinaryRelevant() {
    return binaryRelevant;
  }

  public int getCount() {
    return count;
  }

  public int getRelevant() {
    return relevant;
  }
}