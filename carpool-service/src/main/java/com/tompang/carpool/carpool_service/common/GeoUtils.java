package com.tompang.carpool.carpool_service.common;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.tompang.carpool.carpool_service.command.domain.LatLong;

public class GeoUtils {
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static Point createPoint(LatLong latLong) {
        return createPoint(latLong.latitude, latLong.longitude);    
    }

    public static Point createPoint(double latitude, double longitude) {
        // Note: Coordinate takes (x, y) = (longitude, latitude)
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(4326); // WGS84
        return point;
    }
}