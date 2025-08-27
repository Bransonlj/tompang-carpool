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
    public String failedReason;
    
    public String signedImageUrl;

    public static DriverRegistrationResponseDto fromEntity(DriverRegistration driver) {
        return DriverRegistrationResponseDto.builder()
            .id(driver.getId())
            .userId(driver.getUserId())
            .vehicleRegistrationNumber(driver.getVehicleRegistrationNumber())
            .vehicleMake(driver.getVehicleMake())
            .vehicleModel(driver.getVehicleModel())
            .createdAt(driver.getCreatedAt())
            .status(driver.getRegistrationStatus())
            .failedReason(driver.getFailedReason())
            .build();
    }
}
