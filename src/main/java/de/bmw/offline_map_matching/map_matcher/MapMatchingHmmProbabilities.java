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

package de.bmw.offline_map_matching.map_matcher;

import java.util.Date;

import de.bmw.hmm.HmmProbabilities;

/**
 * Based on Newson, Paul, and John Krumm. "Hidden Markov map matching through noise and sparseness."
 * Proceedings of the 17th ACM SIGSPATIAL International Conference on Advances in Geographic
 * Information Systems. ACM, 2009.
 */
public class MapMatchingHmmProbabilities implements
        HmmProbabilities<RoadPosition, GpsMeasurement> {

    /**
     * Standard deviation of the normal distribution used for modeling the GPS error taken from
     * Newson, Krumm.
     */
    private final static double SIGMA_MEASUREMENT_PROBABILITY = 4.07;

    /**
     * Beta parameter of the exponential distribution for modeling transition probabilities.
     * Empirically computed from the Microsoft ground truth data for shortest route lengths and
     * 60 s sampling interval but also works for other sampling intervals.
     *
     * @see MapMatchingHmmProbabilities#normalizedTransitionMetric(RoadPosition, RoadPosition)
     */
    private final static double BETA_TRANSITION_PROBABILITY = 0.00959442;

    private final SpatialMetrics metrics;

    public MapMatchingHmmProbabilities(SpatialMetrics metrics) {
        if (metrics == null) {
            throw new NullPointerException();
        }
        this.metrics = metrics;
    }

    /**
     * Returns the logarithmic emission probability density.
     */
    @Override
    public double emissionLogProbability(RoadPosition roadPosition, GpsMeasurement measurement) {
        return Math.log(Distributions.normalDistribution(SIGMA_MEASUREMENT_PROBABILITY,
                metrics.gpsDistance(roadPosition)));
    }

    /**
     * Returns the logarithmic transition log probability density.
     */
    @Override
    public double transitionLogProbability(RoadPosition sourcePosition,
            RoadPosition targetPosition) {
        Double transitionMetric = normalizedTransitionMetric(sourcePosition, targetPosition);
        if (transitionMetric == null) {
            return Double.NEGATIVE_INFINITY;
        } else {
            return Distributions.logExponentialDistribution(
                    MapMatchingHmmProbabilities.BETA_TRANSITION_PROBABILITY, transitionMetric);
        }
    }

    /**
     * Returns |linearDistance - shortestRouteLength| / time_difference² in [m/s²], where
     * linearDistance is the linear distance between the corresponding GPS measurements of
     * sourcePositon and targetPosition, shortestRouteLength is the shortest route length from
     * sourcePosition to targetPosition on the road network and timeDifference is the time
     * difference between the corresponding GPS measurements ofsourcePosition and targetPosition.
     *
     * In contrast to Newson & Krumm the absolute distance difference is divided by the quadratic
     * time difference to make the beta parameter of the exponential distribution independent of the
     * sampling interval.
     */
    public Double normalizedTransitionMetric(RoadPosition sourcePosition,
            RoadPosition targetPosition) {
        final Date sourceTime = sourcePosition.gpsMeasurement.time;
        final Date targetTime = targetPosition.gpsMeasurement.time;
        if (!sourceTime.before(targetTime)) {
            throw new IllegalStateException(
                    "Time difference between subsequent GPS measurements must be >= 0.");
        }

        final double linearDistance = metrics.linearDistance(sourcePosition.gpsMeasurement,
                targetPosition.gpsMeasurement);
        final Double routeLength = metrics.routeLength(sourcePosition, targetPosition);
        if (routeLength == null) {
            return null;
        } else {
            double timeDifferenceS = (targetTime.getTime() - sourceTime.getTime()) / 1000.0;
            return Math.abs(linearDistance - routeLength) / (timeDifferenceS * timeDifferenceS);
        }
    }


}
