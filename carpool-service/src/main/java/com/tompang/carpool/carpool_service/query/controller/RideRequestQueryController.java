package com.tompang.carpool.carpool_service.query.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.carpool_service.common.exceptions.ResourceNotFoundException;
import com.tompang.carpool.carpool_service.query.dto.RideRequestDetailedDto;
import com.tompang.carpool.carpool_service.query.dto.RideRequestSummaryDto;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.service.RideRequestQueryService;

@RestController
@RequestMapping("api/ride-request/query")
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

    @GetMapping("/rider/{id}")
    public ResponseEntity<List<RideRequestSummaryDto>> getRiderRequestsByRiderId(@PathVariable String id) {
        List<RideRequest> requests = queryService.getRideRequestsByRiderId(id);
        List<RideRequestSummaryDto> requestDtos = requests.stream().map(request -> RideRequestSummaryDto.fromEntity(request)).toList();
        return ResponseEntity.ok(requestDtos);
    }
}
