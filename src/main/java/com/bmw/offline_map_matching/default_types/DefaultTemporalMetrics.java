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

package com.bmw.offline_map_matching.default_types;

import com.bmw.offline_map_matching.map_matcher.TemporalMetrics;

/**
 * Implements the temporal metrics for the default spatial types defined in this package.
  */
public class DefaultTemporalMetrics implements TemporalMetrics<GpsMeasurement> {

    /**
     * @see TemporalMetrics#timeDifference(Object, Object)
     */
    @Override
    public double timeDifference(GpsMeasurement m1, GpsMeasurement m2) {
        return (m2.time.getTime() - m1.time.getTime()) / 1000.0;
    }

}
