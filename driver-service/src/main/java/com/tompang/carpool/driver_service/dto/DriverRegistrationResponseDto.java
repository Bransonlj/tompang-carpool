package com.tompang.carpool.driver_service.dto;

import java.time.LocalDateTime;

import com.tompang.carpool.driver_service.model.DriverRegistration;
import com.tompang.carpool.driver_service.model.RegistrationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverRegistrationResponseDto {
    public String id;
    public String userId;
    public String vehicleRegistrationNumber;
    public String vehicleMake;
    public String vehicleModel;
    public LocalDateTime createdAt;
    public RegistrationStatus status;
    public String rejectedReason; // null if not failed status
    public String signedImageUrl; // null if not provided

    public static DriverRegistrationResponseDto fromEntity(DriverRegistration driver) {
        DriverRegistrationResponseDto dto = DriverRegistrationResponseDto.builder()
            .id(driver.getId())
            .userId(driver.getUserId())
            .vehicleRegistrationNumber(driver.getVehicleRegistrationNumber())
            .vehicleMake(driver.getVehicleMake())
            .vehicleModel(driver.getVehicleModel())
            .createdAt(driver.getCreatedAt())
            .status(driver.getRegistrationStatus())
            .build();
        
        if (driver.getRegistrationStatus().equals(RegistrationStatus.FAILED)) {
            if (driver.getManualReview() != null && driver.getManualReview().getFailReason() != null) {
                dto.setRejectedReason(driver.getManualReview().getFailReason());
            } else {
                // automatically rejected
                dto.setRejectedReason("Automatic validation failed");
            }
        }

        return dto;
    }
}
