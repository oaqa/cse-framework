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

import java.util.Collections;
import java.util.List;
import java.util.Set;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class EvaluationHelper {

  public static Set<String> intersect(Set<String> docs, Set<String> gs) {
    Set<String> relevant = Sets.newHashSet(gs);
    relevant.retainAll(docs);
    return relevant;
  }

  public static float getF1(float p, float r) {
    return ((p + r) != 0) ? (2 * p * r) / (p + r) : 0;
  }

  public static <T> Set<String> getStringSet(List<T> results, Function<T, String> toIdString) {
    Set<String> set = Sets.newHashSet();
    for (T result : results) {
      set.add(toIdString.apply(result));
    }
    return set;
  }

//  # function to find unique pmids for all topicids in a dictionary,
//  # it is important that this function preserves rank order!
//  def buildUniquePmidsByTopic(submissionDx):
//     uniquePmidsByTopic = {}
//     for topic in submissionDx.keys():
//         quartets = submissionDx[topic]
//         quartets.sort() #sort the list by rank, then by pmid, then offset and then by length
//         tempLst = []
//         for quartet in quartets:
//             pmid    =    quartet[1]
//             tempLst.append(pmid)
//         # remove duplications, preserving order...
//         uniquePmidsByTopic[topic] = removeDups(tempLst)
//     return uniquePmidsByTopic
  
  public static <T> List<String> getUniqeDocIdList(List<T> results, Ordering<T> ordering, Function<T, String> toIdString) {
    Collections.sort(results, ordering);
    Set<String> list = Sets.newLinkedHashSet();
    for (T result : results) {
      list.add(toIdString.apply(result));
    }
    return Lists.newArrayList(list);
  }
  
  public static float getAvgMAP(List<String> docsArray, Set<String> gsSet) {
    int hit = 0;
    float totalPrec = 0;
    for (int i = 0; i < docsArray.size(); ++i) {
      if (gsSet.contains(docsArray.get(i))) {
        hit++;
        totalPrec += (float) hit / (i + 1);
      }
    }
    if (hit > 0) {
      //System.out.printf("%s/%s=%s\n", totalPrec, gsSet.size(), totalPrec / gsSet.size());
      totalPrec /= gsSet.size();
    }
    return totalPrec;
  }
}
