package com.tompang.carpool.carpool_service.query.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.carpool_service.query.dto.CarpoolDetailedDto;
import com.tompang.carpool.carpool_service.query.dto.CarpoolSummaryDto;
import com.tompang.carpool.carpool_service.query.service.CarpoolQueryService;

@RestController
@RequestMapping("api/carpool/query")
public class CarpoolQueryController {

    private final CarpoolQueryService carpoolQueryService;

    public CarpoolQueryController(CarpoolQueryService carpoolQueryService) {
        this.carpoolQueryService = carpoolQueryService;
    }

    @GetMapping("{id}")
    public ResponseEntity<CarpoolDetailedDto> getCarpoolById(@PathVariable String id) {
        CarpoolDetailedDto dto = carpoolQueryService.getCarpoolById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("driver/{id}")
    public ResponseEntity<List<CarpoolSummaryDto>> getCarpoolByDriverId(@PathVariable String id) {
        List<CarpoolSummaryDto> carpoolDtos = carpoolQueryService.getCarpoolByDriverId(id);
        return ResponseEntity.ok(carpoolDtos);
    }

}
