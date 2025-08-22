package com.tompang.carpool.carpool_service.query.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.carpool_service.common.exceptions.ResourceNotFoundException;
import com.tompang.carpool.carpool_service.query.dto.CarpoolDetailedDto;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.service.CarpoolQueryService;

@RestController
@RequestMapping("api/carpool/query")
public class CarpoolQueryController {

    private final CarpoolQueryService carpoolQueryService;

    public CarpoolQueryController(CarpoolQueryService carpoolQueryService) {
        this.carpoolQueryService = carpoolQueryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarpoolDetailedDto> getCarpoolById(@PathVariable String id) {
        Optional<Carpool> carpool = carpoolQueryService.getCarpoolById(id);
        if (carpool.isEmpty()) {
            throw new ResourceNotFoundException("Carpool not found: " + id);
        }

        return ResponseEntity.ok(CarpoolDetailedDto.fromEntity(carpool.get()));
    }

}
