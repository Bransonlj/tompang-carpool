package com.tompang.carpool.carpool_service.query.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tompang.carpool.carpool_service.query.entity.Carpool;

public interface CarpoolQueryRepository extends JpaRepository<Carpool, String> {

    @Query(
        value = "SELECT * FROM carpool " + 
            "WHERE origin= :origin " + 
            "AND destination=:destination " + 
            "AND arrival_time BETWEEN :startTime AND :endTime " + 
            "AND total_seats >= seats_assigned + :seatsNeeded",
        nativeQuery = true
    )
    List<Carpool> findCarpoolsByRouteInTimeRangeWithSeats(
        @Param("origin") String origin,
        @Param("destination") String destination,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("seatsNeeded") int seatsNeeded
    );
}
