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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bmw.hmm_lib.HmmProbabilities;
import com.bmw.hmm_lib.TimeStep;
import com.bmw.offline_map_matching.default_types.RoadPosition;

/**
 * Based on Newson, Paul, and John Krumm. "Hidden Markov map matching through noise and sparseness."
 * Proceedings of the 17th ACM SIGSPATIAL International Conference on Advances in Geographic
 * Information Systems. ACM, 2009.
 *
 * @param <S> road position type, which corresponds to the HMM state.
 * @param <O> location measurement type, which corresponds to the HMM observation.
 */
public class MapMatchingHmmProbabilities<S, O> implements HmmProbabilities<S, O> {

    /**
     * Maps road positions to coordinate measurements. This is needed because the normalized
     * transition metric uses distance between measurements.
     */
    private final Map<S, O> measurementMap = new HashMap<>();

    private final SpatialMetrics<S, O> spatialMetrics;
    private final TemporalMetrics<O> temporalMetrics;

    /**
     * Standard deviation of the normal distribution [m] used for modeling the GPS error taken from
     * Newson&Krumm.
     */
    private final static double SIGMA = 4.07;

    /**
     * Beta parameter of the exponential distribution for modeling transition probabilities.
     * Empirically computed from the Microsoft ground truth data for shortest route lengths and
     * 60 s sampling interval but also works for other sampling intervals.
     *
     * @see MapMatchingHmmProbabilities#normalizedTransitionMetric(RoadPosition, RoadPosition)
     */
    private final static double BETA = 0.00959442;

    public MapMatchingHmmProbabilities(List<TimeStep<S, O>> timeSteps,
            SpatialMetrics<S, O> spatialMetrics, TemporalMetrics<O> temporalMetrics) {
        if (timeSteps == null || spatialMetrics == null || temporalMetrics == null) {
            throw new NullPointerException();
        }
        for (TimeStep<S, O> timeStep : timeSteps) {
            for (S candidate : timeStep.candidates) {
                measurementMap.put(candidate, timeStep.observation);
            }
        }
        this.spatialMetrics = spatialMetrics;
        this.temporalMetrics = temporalMetrics;
    }

    /**
     * Returns the logarithmic emission probability density.
     */
    @Override
    public double emissionLogProbability(S roadPosition, O measurement) {
        return Math.log(Distributions.normalDistribution(
                MapMatchingHmmProbabilities.SIGMA,
                spatialMetrics.measurementDistance(roadPosition, measurement)));
    }

    /**
     * Returns the logarithmic transition probability density.
     */
    @Override
    public double transitionLogProbability(S sourcePosition, O sourceMeasurement,
    		S targetPosition, O targetMeasurement) {
        Double transitionMetric = normalizedTransitionMetric(sourcePosition, targetPosition);
        if (transitionMetric == null) {
            return Double.NEGATIVE_INFINITY;
        } else {
            return Distributions.logExponentialDistribution(
                    MapMatchingHmmProbabilities.BETA, transitionMetric);
        }
    }

    /**
     * Returns |linearDistance - shortestRouteLength| / time_difference² in [m/s²], where
     * linearDistance is the linear distance between the corresponding location measurements of
     * sourcePositon and targetPosition, shortestRouteLength is the shortest route length from
     * sourcePosition to targetPosition on the road network and timeDifference is the time
     * difference between the corresponding location measurements of sourcePosition and
     * targetPosition.
     *
     * Returns null if there is no route between sourcePosition and targetPosition.
     *
     * In contrast to Newson & Krumm the absolute distance difference is divided by the quadratic
     * time difference to make the beta parameter of the exponential distribution independent of the
     * sampling interval.
     */
    public Double normalizedTransitionMetric(S sourcePosition, S targetPosition) {
        final O sourceMeasurement = measurementMap.get(sourcePosition);
        final O targetMeasurement = measurementMap.get(targetPosition);
        final double timeDiff = temporalMetrics.timeDifference(sourceMeasurement, targetMeasurement);
        if (timeDiff < 0.0) {
            throw new IllegalStateException(
                    "Time difference between subsequent location measurements must be >= 0.");
        }

        final double linearDistance = spatialMetrics.linearDistance(sourceMeasurement,
                targetMeasurement);
        final Double routeLength = spatialMetrics.routeLength(sourcePosition, targetPosition);
        if (routeLength == null) {
            return null;
        } else {
            return Math.abs(linearDistance - routeLength) / (timeDiff * timeDiff);
        }
    }


}
