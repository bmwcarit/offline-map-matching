package com.bmw.offline_map_matching.default_types;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.bmw.offline_map_matching.default_types.EuclideanSpatialMetrics;
import com.bmw.offline_map_matching.default_types.GpsMeasurement;

public class EuclideanSpatialMetricsTest {

    final double DELTA = 1e-8;

    @Test
    public void testDistance() {
        GpsMeasurement m1 = new GpsMeasurement(new Date(), 0.0, 0.0);
        GpsMeasurement m2 = new GpsMeasurement(new Date(), 30.0, 40.0);

        EuclideanSpatialMetrics metrics = new EuclideanSpatialMetrics();
        assertEquals(50.0, metrics.linearDistance(m1, m2), DELTA);
    }

}
