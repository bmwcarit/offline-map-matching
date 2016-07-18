/**
 * Copyright (C) 2015, BMW Car IT GmbH
 * Author: Stefan Holder (stefan.holder@bmw.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bmw.offline_map_matching.map_matcher;

import java.util.List;

import com.bmw.hmm_lib.Hmm;
import com.bmw.hmm_lib.MostLikelySequence;
import com.bmw.hmm_lib.TimeStep;

public class OfflineMapMatcher {

    /**
     * Returns the most likely sequence of map matched location measurements or null if there was an
     * HMM break.
     */
    public static <S, O> MostLikelySequence<S, O> computeMostLikelySequence(
            List<TimeStep<S, O>> timeSteps, TemporalMetrics<O> temporalMetrics,
            SpatialMetrics<S, O> spatialMetrics) {
        MapMatchingHmmProbabilities<S, O> probabilities =
                new MapMatchingHmmProbabilities<>(timeSteps, spatialMetrics, temporalMetrics);
        return Hmm.computeMostLikelySequence(probabilities, timeSteps.iterator());
    }

}
