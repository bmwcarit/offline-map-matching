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
 *
 * @param <S> road position type, which corresponds to the HMM state.
 * @param <O> location measurement type, which corresponds to the HMM observation.
 */
public class PrecomputedSpatialMetrics<S, O> implements SpatialMetrics<S, O> {


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

    private final Map<S, Double> measurementDistances = new HashMap<>();
    private final Map<KeyPair<O, O>, Double> linearDistances =
            new HashMap<>();
    private final Map<KeyPair<S, S>, Double> routeLengths = new HashMap<>();

    /**
     * Note that the passed road position must be the same instance passed to the HMM via
     * {@link TimeStep}s
     */
    public void addMeasurementDistance(S roadPosition, double measurementDistance) {
        if (measurementDistances.containsKey(roadPosition)) {
            throw new IllegalArgumentException();
        }

        measurementDistances.put(roadPosition, measurementDistance);
    }

    public void addLinearDistance(O formerMeasurement,
            O laterMeasurement, double linearDistance) {
        KeyPair<O, O> keyPair =
                new KeyPair<>(formerMeasurement, laterMeasurement);
        if (linearDistances.containsKey(keyPair)) {
            throw new IllegalArgumentException();
        }
        linearDistances.put(keyPair, linearDistance);
    }

    /**
     * @param routeLength Pass null if there is no route between both road positions.
     */
    public void addRouteLength(S sourcePosition, S targetPosition,
            Double routeLength) {
        KeyPair<S, S> keyPair = new KeyPair<>(sourcePosition, targetPosition);
        if (linearDistances.containsKey(keyPair)) {
            throw new IllegalArgumentException();
        }
        routeLengths.put(keyPair, routeLength);
    }

    @Override
    public double measurementDistance(S roadPosition) {
        Double result = measurementDistances.get(roadPosition);
        if (result == null) {
            throw new IllegalStateException();
        }
        return result;
    }

    @Override
    public double linearDistance(O formerMeasurement, O laterMeasurement) {
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
    public Double routeLength(S sourcePosition, S targetPosition) {
        if (!routeLengths.containsKey(new KeyPair<>(sourcePosition, targetPosition))) {
            throw new IllegalStateException();
        }
        return routeLengths.get(new KeyPair<>(sourcePosition, targetPosition));
    }


}
