package com.tompang.carpool.carpool_service.query.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.common.exceptions.ResourceNotFoundException;
import com.tompang.carpool.carpool_service.query.dto.RideRequestDetailedDto;
import com.tompang.carpool.carpool_service.query.dto.RideRequestSummaryDto;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;

@Service
public class RideRequestQueryService {

    private final RideRequestQueryRepository repository;
    private final S3Service s3Service;

    private void fillOriginAndDestinationImageUrl(RideRequestSummaryDto dto, RideRequest request) {
        if (request.getOriginImageKey() != null) {
            dto.setOriginImageUrl(s3Service.getFileUrl(request.getOriginImageKey()));
        }

        if (request.getDestinationImageKey() != null) {
            dto.setDestinationImageUrl(s3Service.getFileUrl(request.getDestinationImageKey()));
        }
    }

    public RideRequestQueryService(RideRequestQueryRepository repository, S3Service s3Service) {
        this.repository = repository;
        this.s3Service = s3Service;
    }

    public RideRequestDetailedDto getRideRequestById(String id) {
        RideRequest request = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ride Request not found: " + id));
        RideRequestDetailedDto dto = RideRequestDetailedDto.fromEntity(request);
        fillOriginAndDestinationImageUrl(dto, request);
        return dto;
    }

    public List<RideRequestSummaryDto> getRideRequestsByRiderId(String id) {
        List<RideRequest> requests = repository.findAllByRiderId(id);
        List<RideRequestSummaryDto> dtos = new ArrayList<>();
        RideRequestSummaryDto dto;
        for (RideRequest request : requests) {
            dto = RideRequestSummaryDto.fromEntity(request);
            fillOriginAndDestinationImageUrl(dto, request);
            dtos.add(dto);
        }

        return dtos;
    }
}
