package com.tompang.carpool.carpool_service.query.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.carpool_service.common.exceptions.ResourceNotFoundException;
import com.tompang.carpool.carpool_service.query.dto.RideRequestDetailedDto;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.service.RideRequestQueryService;

@RestController
@RequestMapping("/query/ride-request")
public class RideRequestQueryController {
    private final RideRequestQueryService queryService;

    public RideRequestQueryController(RideRequestQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideRequestDetailedDto> getRideRequestById(@PathVariable String id) {
        Optional<RideRequest> request = queryService.getRideRequestById(id);
        if (request.isEmpty()) {
            throw new ResourceNotFoundException("Ride Request not found: " + id);
        }

        return ResponseEntity.ok(RideRequestDetailedDto.fromEntity(request.get()));
    }
}
