package com.tompang.carpool.driver_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.driver_service.common.AuthHeader;
import com.tompang.carpool.driver_service.dto.DriverRegistrationResponseDto;
import com.tompang.carpool.driver_service.dto.admin.ManualRejectRequestDto;
import com.tompang.carpool.driver_service.exception.AccessDeniedException;
import com.tompang.carpool.driver_service.service.DriverAdminService;
import com.tompang.carpool.driver_service.service.S3Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/driver/admin")
public class AdminController {

    private final DriverAdminService adminService;
    private final S3Service s3Service;

    public AdminController(DriverAdminService adminService, S3Service s3Service) {
        this.adminService = adminService;
        this.s3Service = s3Service;
    }

    private void verifyAdminRole(String roles) {
        if (!AuthHeader.hasAdminRole(roles)) {
            throw new AccessDeniedException("Must have ADMIN user role");
        }
    }

    @GetMapping("pending-review")
    public ResponseEntity<List<DriverRegistrationResponseDto>> getAllPendingManualReview(
        @RequestHeader(name = AuthHeader.USER_ID) String userId,
        @RequestHeader(name = AuthHeader.USER_ROLES) String roles
    ) {
        verifyAdminRole(roles);
        return ResponseEntity.ok().body(
            adminService.getAllPendingManualReview()
                .stream()
                .map(registration -> {
                    DriverRegistrationResponseDto dto = DriverRegistrationResponseDto.fromEntity(registration);
                    dto.setSignedImageUrl(s3Service.getFileUrl(S3Service.Key.builder()
                            .dir(S3Service.DRIVER_FOLDER)
                            .id(registration.getId())
                            .build()));
                    return dto;
                })
                .toList());
    }

    @PostMapping("{id}/approve")
    public ResponseEntity<Void> manuallyApproveDriverRegistration(
        @RequestHeader(name = AuthHeader.USER_ID) String userId,
        @RequestHeader(name = AuthHeader.USER_ROLES) String roles,
        @PathVariable("id") String driverRegistrationId
    ) {
        verifyAdminRole(roles);
        adminService.manuallyApproveDriverRegistration(driverRegistrationId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/reject")
    public ResponseEntity<Void> manuallyRejectDriverRegistration(
        @RequestHeader(name = AuthHeader.USER_ID) String userId,
        @RequestHeader(name = AuthHeader.USER_ROLES) String roles,
        @PathVariable("id") String driverRegistrationId,
        @RequestBody @Valid ManualRejectRequestDto dto
    ) {
        verifyAdminRole(roles);
        adminService.manuallyRejectDriverRegistration(driverRegistrationId, userId, dto.getRejectReason());
        return ResponseEntity.noContent().build();
    }

}
