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
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bmw.hmm_lib.SequenceState;
import com.bmw.hmm_lib.Transition;
import com.bmw.hmm_lib.ViterbiAlgorithm;
import com.bmw.offline_map_matching.HmmProbabilities;
import com.bmw.offline_map_matching.TimeStep;
import com.bmw.offline_map_matching.types.GpsMeasurement;
import com.bmw.offline_map_matching.types.Point;
import com.bmw.offline_map_matching.types.RoadPath;
import com.bmw.offline_map_matching.types.RoadPosition;

/**
 * This class demonstrate how to use the hmm-lib for map matching. The methods
 * of this class can be used as a template to implement map matching for an actual map.
 *
 * The test scenario is depicted in ./OfflineMapMatcherTest.png.
 * All road segments can be driven in both directions. The orientation of road segments
 * is needed to determine the fractions of a road positions.
 */
public class OfflineMapMatcherTest {

    private final HmmProbabilities<RoadPosition, GpsMeasurement> hmmProbabilities =
            new HmmProbabilities<>();

    private final static Map<GpsMeasurement, Collection<RoadPosition>> candidateMap =
            new HashMap<>();

    private final static Map<Transition<RoadPosition>, Double> routeLengths = new HashMap<>();

    private final static GpsMeasurement gps1 = new GpsMeasurement(seconds(0), 10, 10);
    private final static GpsMeasurement gps2 = new GpsMeasurement(seconds(1), 30, 20);
    private final static GpsMeasurement gps3 = new GpsMeasurement(seconds(2), 30, 40);
    private final static GpsMeasurement gps4 = new GpsMeasurement(seconds(3), 10, 70);

    private final static RoadPosition rp11 = new RoadPosition(1, 1.0 / 5.0, 20.0, 10.0);
    private final static RoadPosition rp12 = new RoadPosition(2, 1.0 / 5.0, 60.0, 10.0);
    private final static RoadPosition rp21 = new RoadPosition(1, 2.0 / 5.0, 20.0, 20.0);
    private final static RoadPosition rp22 = new RoadPosition(2, 2.0 / 5.0, 60.0, 20.0);
    private final static RoadPosition rp31 = new RoadPosition(1, 5.0 / 6.0, 20.0, 40.0);
    private final static RoadPosition rp32 = new RoadPosition(3, 1.0 / 4.0, 30.0, 50.0);
    private final static RoadPosition rp33 = new RoadPosition(2, 5.0 / 6.0, 60.0, 40.0);
    private final static RoadPosition rp41 = new RoadPosition(4, 2.0 / 3.0, 20.0, 70.0);
    private final static RoadPosition rp42 = new RoadPosition(5, 2.0 / 3.0, 60.0, 70.0);

    @BeforeClass
    public static void setUpClass() {
        candidateMap.put(gps1, Arrays.asList(rp11, rp12));
        candidateMap.put(gps2, Arrays.asList(rp21, rp22));
        candidateMap.put(gps3, Arrays.asList(rp31, rp32, rp33));
        candidateMap.put(gps4, Arrays.asList(rp41, rp42));

        addRouteLength(rp11, rp21, 10.0);
        addRouteLength(rp11, rp22, 110.0);
        addRouteLength(rp12, rp21, 110.0);
        addRouteLength(rp12, rp22, 10.0);

        addRouteLength(rp21, rp31, 20.0);
        addRouteLength(rp21, rp32, 40.0);
        addRouteLength(rp21, rp33, 80.0);
        addRouteLength(rp22, rp31, 80.0);
        addRouteLength(rp22, rp32, 60.0);
        addRouteLength(rp22, rp33, 20.0);

        addRouteLength(rp31, rp41, 30.0);
        addRouteLength(rp31, rp42, 70.0);
        addRouteLength(rp32, rp41, 30.0);
        addRouteLength(rp32, rp42, 50.0);
        addRouteLength(rp33, rp41, 70.0);
        addRouteLength(rp33, rp42, 30.0);
    }

    private static Date seconds(int seconds) {
        Calendar c = new GregorianCalendar(2014, 1, 1);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    private static void addRouteLength(RoadPosition from, RoadPosition to, double routeLength) {
        routeLengths.put(new Transition<RoadPosition>(from, to), routeLength);
    }

    /*
     * Returns the Cartesian distance between two points.
     * For real map matching applications, one would compute the great circle distance between
     * two GPS points.
     */
    private double computeDistance(Point p1, Point p2) {
        final double xDiff = p1.x - p2.x;
        final double yDiff = p1.y - p2.y;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    /*
     * For real map matching applications, candidates would be computed using a radius query.
     */
    private Collection<RoadPosition> computeCandidates(GpsMeasurement gpsMeasurement) {
        return candidateMap.get(gpsMeasurement);
    }

    private void computeEmissionProbabilities(
            TimeStep<RoadPosition, GpsMeasurement, RoadPath> timeStep) {
        for (RoadPosition candidate : timeStep.candidates) {
            final double distance =
                    computeDistance(candidate.position, timeStep.observation.position);
            timeStep.addEmissionLogProbability(candidate,
                    hmmProbabilities.emissionLogProbability(distance));
        }
    }

    private void computeTransitionProbabilities(
            TimeStep<RoadPosition, GpsMeasurement, RoadPath> prevTimeStep,
            TimeStep<RoadPosition, GpsMeasurement, RoadPath> timeStep) {
        for (RoadPosition from : prevTimeStep.candidates) {
            for (RoadPosition to : timeStep.candidates) {

                // For real map matching applications, route lengths and road paths would be
                // computed using a router. The most efficient way is to use a single-source
                // multi-target router.
                final double routeLength = routeLengths.get(new Transition<RoadPosition>(from, to));
                timeStep.addRoadPath(from, to, new RoadPath(from, to));

                final double linearDistance = computeDistance(from.position, to.position);
                final double timeDiff = (timeStep.observation.time.getTime() -
                        prevTimeStep.observation.time.getTime()) / 1000.0;

                final double transitionLogProbability = hmmProbabilities.transitionLogProbability(
                        routeLength, linearDistance, timeDiff);
                timeStep.addTransitionLogProbability(from, to, transitionLogProbability);
            }
        }
    }

    @Test
    public void testMapMatching() {
        final List<GpsMeasurement> gpsMeasurements = Arrays.asList(gps1, gps2, gps3, gps4);

        ViterbiAlgorithm<RoadPosition, GpsMeasurement, RoadPath> viterbi =
                new ViterbiAlgorithm<>();
        TimeStep<RoadPosition, GpsMeasurement, RoadPath> prevTimeStep = null;
        for (GpsMeasurement gpsMeasurement : gpsMeasurements) {
            final Collection<RoadPosition> candidates = computeCandidates(gpsMeasurement);
            final TimeStep<RoadPosition, GpsMeasurement, RoadPath> timeStep =
                    new TimeStep<>(gpsMeasurement, candidates);
            computeEmissionProbabilities(timeStep);
            if (prevTimeStep == null) {
                viterbi.startWithInitialObservation(timeStep.observation, timeStep.candidates,
                        timeStep.emissionLogProbabilities);
            } else {
                computeTransitionProbabilities(prevTimeStep, timeStep);
                viterbi.nextStep(timeStep.observation, timeStep.candidates,
                        timeStep.emissionLogProbabilities, timeStep.transitionLogProbabilities,
                        timeStep.roadPaths);
            }
            prevTimeStep = timeStep;
        }

        List<SequenceState<RoadPosition, GpsMeasurement, RoadPath>> roadPositions =
                viterbi.computeMostLikelySequence();

        assertFalse(viterbi.isBroken());
        List<SequenceState<RoadPosition, GpsMeasurement, RoadPath>> expected = new ArrayList<>();
        expected.add(new SequenceState<>(rp11, gps1, (RoadPath) null));
        expected.add(new SequenceState<>(rp21, gps2, new RoadPath(rp11, rp21)));
        expected.add(new SequenceState<>(rp31, gps3, new RoadPath(rp21, rp31)));
        expected.add(new SequenceState<>(rp41, gps4, new RoadPath(rp31, rp41)));
        assertEquals(expected, roadPositions);
    }

}
