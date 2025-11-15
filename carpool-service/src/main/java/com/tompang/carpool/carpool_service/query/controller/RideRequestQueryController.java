package com.tompang.carpool.carpool_service.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.carpool_service.query.dto.RideRequestDetailedDto;
import com.tompang.carpool.carpool_service.query.dto.RideRequestSummaryDto;
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
        RideRequestDetailedDto dto = queryService.getRideRequestById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/rider/{id}")
    public ResponseEntity<List<RideRequestSummaryDto>> getRiderRequestsByRiderId(@PathVariable String id) {
        List<RideRequestSummaryDto> requestDtos = queryService.getRideRequestsByRiderId(id);
        return ResponseEntity.ok(requestDtos);
    }
}
