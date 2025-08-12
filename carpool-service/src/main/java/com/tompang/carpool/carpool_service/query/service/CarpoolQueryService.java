package com.tompang.carpool.carpool_service.query.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public List<Carpool> getCarpoolsByRouteInTimeRangeWithSeats(
        String origin,
        String destination,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int seatsNeeded
    ) {
        return repository.findCarpoolsByRouteInTimeRangeWithSeats(origin, destination, startTime, endTime, seatsNeeded);
    }
}
