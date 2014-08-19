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

import java.util.HashMap;
import java.util.Map;

import de.bmw.hmm.TimeStep;

/**
 * Stores precomputed {@link SpatialMetrics}.
 */
public class PrecomputedSpatialMetrics implements SpatialMetrics {


    /**
    * Map keys for storing precomputed spatial metrics.
    *
    * @param <A> Type of first element (_1).
    * @param <B> Type of second element (_2).
    */
    private static class KeyPair<A, B> {
        public final A _1;
        public final B _2;

        public KeyPair(A _1, B _2) {
            if (_1 == null || _2 == null) {
                throw new NullPointerException();
            }
            this._1 = _1;
            this._2 = _2;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + _1.hashCode();
            result = prime * result + _2.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            KeyPair<?,?> other = (KeyPair<?,?>) obj;
            return (_1.equals(other._1) && _2.equals(other._2));
        }
    }

    private final Map<RoadPosition, Double> gpsDistances = new HashMap<>();
    private final Map<KeyPair<GpsMeasurement, GpsMeasurement>, Double> linearDistances =
            new HashMap<>();
    private final Map<KeyPair<RoadPosition, RoadPosition>, Double> routeLengths = new HashMap<>();

    /**
     * Note that the passed road position must be the same instance passed to the HMM via
     * {@link TimeStep}s
     */
    public void addGpsDistance(RoadPosition roadPosition, double gpsDistance) {
        if (gpsDistances.containsKey(roadPosition)) {
            throw new IllegalArgumentException();
        }

        gpsDistances.put(roadPosition, gpsDistance);
    }

    public void addLinearDistance(GpsMeasurement formerMeasurement,
            GpsMeasurement laterMeasurement, double linearDistance) {
        if (!formerMeasurement.time.before(laterMeasurement.time)) {
            throw new IllegalArgumentException();
        }

        KeyPair<GpsMeasurement, GpsMeasurement> keyPair =
                new KeyPair<>(formerMeasurement, laterMeasurement);
        if (linearDistances.containsKey(keyPair)) {
            throw new IllegalArgumentException();
        }
        linearDistances.put(keyPair, linearDistance);
    }

    /**
     * @param routeLength Pass null if there is no route between both road positions.
     */
    public void addRouteLength(RoadPosition sourcePosition, RoadPosition targetPosition,
            Double routeLength) {
        KeyPair<RoadPosition, RoadPosition> keyPair = new KeyPair<>(sourcePosition, targetPosition);
        if (routeLengths.containsKey(keyPair)) {
            throw new IllegalArgumentException();
        }
        routeLengths.put(keyPair, routeLength);
    }

    @Override
    public double gpsDistance(RoadPosition roadPosition) {
        Double result = gpsDistances.get(roadPosition);
        if (result == null) {
            throw new IllegalStateException();
        }
        return result;
    }

    @Override
    public double linearDistance(GpsMeasurement formerMeasurement, GpsMeasurement laterMeasurement) {
        if (!formerMeasurement.time.before(laterMeasurement.time)) {
            throw new IllegalArgumentException();
        }
        Double result = linearDistances.get(new KeyPair<>(formerMeasurement, laterMeasurement));
        if (result == null) {
            throw new IllegalStateException();
        }
        return result;
    }

    /**
     * Returns null if there is no route between both road positions.
     */
    @Override
    public Double routeLength(RoadPosition sourcePosition, RoadPosition targetPosition) {
        if (!routeLengths.containsKey(new KeyPair<>(sourcePosition, targetPosition))) {
            throw new IllegalStateException();
        }
        return routeLengths.get(new KeyPair<>(sourcePosition, targetPosition));
    }


}
