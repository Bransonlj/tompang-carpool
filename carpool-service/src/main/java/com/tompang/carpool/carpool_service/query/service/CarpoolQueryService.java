package com.tompang.carpool.carpool_service.query.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;

@Service
public class CarpoolQueryService {

    private final CarpoolQueryRepository repository;

    public CarpoolQueryService(CarpoolQueryRepository repository) {
        this.repository = repository;
    }

    public Optional<Carpool> getCarpoolById(String id) {
        return repository.findById(id);
    }

    public List<Carpool> getCarpoolsByRouteInRangeWithSeats(
        Point origin,
        Point destination,
        double rangeMeters,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int seatsNeeded
    ) {
        return repository.findCarpoolsByRouteInRangeWithSeats(origin, destination, rangeMeters, startTime, endTime, seatsNeeded);
    }

    public List<Carpool> getCarpoolByDriverId(String driverId) {
        return this.repository.findAllByDriverId(driverId);
    }
}
