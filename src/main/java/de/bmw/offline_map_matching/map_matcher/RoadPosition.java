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



/**
 * Position of a vehicle on the road network.
 */
public class RoadPosition {

    /**
     * ID of the edge, on which the vehicle is positioned.
     */
    final public int edgeId;

    /**
     * Position on the edge from beginning as a number in the interval [0,1].
     */
    final public double fraction;

    /**
     * GPS measurement based on which this road position was computed. Needed when the normalized
     * transition metric is computed on demand.
     * @see SpatialMetrics#normalizedTransitionMetric(RoadPosition, RoadPosition)
     */
    final public GpsMeasurement gpsMeasurement;

    public RoadPosition(int edgeId, double fraction, GpsMeasurement gpsMeasurement) {
        if (gpsMeasurement == null) {
            throw new NullPointerException();
        }

        this.edgeId = edgeId;
        this.fraction = fraction;
        this.gpsMeasurement = gpsMeasurement;
    }

    @Override
    public String toString() {
        return "RoadPosition [edgeId=" + edgeId + ", fraction=" + fraction + "]";
    }

}
