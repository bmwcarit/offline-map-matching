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

package com.bmw.offline_map_matching.types;

import java.util.Date;

/**
 * Example type for location coordinates.
 */
public class GpsMeasurement {

    public final Date time;
    
    public Point position;

    public GpsMeasurement(Date time, Point position) {
        this.time = time;
        this.position = position;
    }

    public GpsMeasurement(Date time, double lon, double lat) {
        this(time, new Point(lon, lat));
    }

    @Override
    public String toString() {
        return "GpsMeasurement [time=" + time + ", position=" + position + "]";
    }

}
