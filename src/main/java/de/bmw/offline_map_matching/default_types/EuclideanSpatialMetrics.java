package de.bmw.offline_map_matching.default_types;

import de.bmw.offline_map_matching.map_matcher.PrecomputedSpatialMetrics;

/**
 * Computes distances between GPS locations with Euclidean geometry.
 * Uses the cache mechanism of PrecomputedSpatialMetrics to store and retrieve route lengths.
 */
public class EuclideanSpatialMetrics extends
        PrecomputedSpatialMetrics<RoadPosition, GpsMeasurement> {

    @Override
    public void addMeasurementDistance(RoadPosition roadPosition,
            GpsMeasurement locationMeasurment, double measurementDistance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addLinearDistance(GpsMeasurement formerMeasurement,
            GpsMeasurement laterMeasurement, double linearDistance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double measurementDistance(RoadPosition roadPosition,
            GpsMeasurement measurement) {
        return distance(roadPosition.position, measurement.position);
    }

    @Override
    public double linearDistance(GpsMeasurement formerMeasurement,
            GpsMeasurement laterMeasurement) {
        return distance(formerMeasurement.position, laterMeasurement.position);
    }

    /**
     * Returns the absolute distance between both GPS measurements.
     */
    private double distance(Point p1, Point p2) {
        final double xDiff = p1.x - p2.x;
        final double yDiff = p1.y - p2.y;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

}
