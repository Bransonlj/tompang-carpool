package com.tompang.carpool.carpool_service.query.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tompang.carpool.carpool_service.query.entity.Carpool;

public interface CarpoolQueryRepository extends JpaRepository<Carpool, String> {

    @Query(
        value = "SELECT * FROM carpool " + 
            "WHERE arrival_time BETWEEN :startTime AND :endTime " + 
            "AND total_seats >= seats_assigned + :seatsNeeded " +
            "AND ST_DWithin(origin, :origin, :rangeMeters) = true " + 
            "AND ST_DWithin(destination, :destination, :rangeMeters) = true",
        nativeQuery = true
    )
    List<Carpool> findCarpoolsByRouteInRangeWithSeats(
        @Param("origin") Point origin,
        @Param("destination") Point destination,
        @Param("rangeMeters") double rangeMeters,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("seatsNeeded") int seatsNeeded
    );

    List<Carpool> findAllByDriverId(String driverId);
}
