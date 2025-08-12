package com.tompang.carpool.carpool_service.query.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;

@Service
public class RideRequestQueryService {

    private final RideRequestQueryRepository repository;

    public RideRequestQueryService(RideRequestQueryRepository repository) {
        this.repository = repository;
    }

    public Optional<RideRequest> getRideRequestById(String id) {
        return repository.findById(id);
    }
}
