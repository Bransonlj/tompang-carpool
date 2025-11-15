package com.tompang.carpool.carpool_service.query.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.common.exceptions.ResourceNotFoundException;
import com.tompang.carpool.carpool_service.query.dto.CarpoolDetailedDto;
import com.tompang.carpool.carpool_service.query.dto.CarpoolSummaryDto;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;

@Service
public class CarpoolQueryService {

    private final CarpoolQueryRepository repository;
    private final S3Service s3Service;

    public CarpoolQueryService(CarpoolQueryRepository repository, S3Service s3Service) {
        this.repository = repository;
        this.s3Service = s3Service;
    }

    private void fillOriginAndDestinationImageUrl(CarpoolSummaryDto dto, Carpool carpool) {
        if (carpool.getOriginImageKey() != null) {
            dto.setOriginImageUrl(s3Service.getFileUrl(carpool.getOriginImageKey()));
        }

        if (carpool.getDestinationImageKey() != null) {
            dto.setDestinationImageUrl(s3Service.getFileUrl(carpool.getDestinationImageKey()));
        }
    }

    public CarpoolDetailedDto getCarpoolById(String id) {
        Carpool carpool = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carpool not found: " + id));
        CarpoolDetailedDto dto = CarpoolDetailedDto.fromEntity(carpool);
        fillOriginAndDestinationImageUrl(dto, carpool);
        return dto;
    }

    public List<CarpoolSummaryDto> getCarpoolByDriverId(String driverId) {
        List<Carpool> carpools = this.repository.findAllByDriverId(driverId);
        List<CarpoolSummaryDto> dtos = new ArrayList<>();
        CarpoolSummaryDto dto;
        for (Carpool carpool : carpools) {
            dto = CarpoolSummaryDto.fromEntity(carpool);
            fillOriginAndDestinationImageUrl(dto, carpool);
            dtos.add(dto);
        }

        return dtos;
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
}
