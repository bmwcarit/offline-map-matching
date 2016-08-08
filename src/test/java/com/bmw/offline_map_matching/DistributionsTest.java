/**
 * Copyright (C) 2015-2016, BMW Car IT GmbH and BMW AG
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

package com.bmw.offline_map_matching;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bmw.offline_map_matching.Distributions;

public class DistributionsTest {

    private static double DELTA = 1e-8;

    @Test
    public void testLogNormalDistribution() {
        assertEquals(Math.log(Distributions.normalDistribution(5, 6)),
                Distributions.logNormalDistribution(5, 6), DELTA);
    }

    @Test
    public void testLogExponentialDistribution() {
        assertEquals(Math.log(Distributions.exponentialDistribution(5, 6)),
                Distributions.logExponentialDistribution(5, 6), DELTA);
    }

}
