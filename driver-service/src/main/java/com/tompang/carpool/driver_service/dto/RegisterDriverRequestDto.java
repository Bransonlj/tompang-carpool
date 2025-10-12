package com.tompang.carpool.driver_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDriverRequestDto {
    @NotBlank
    public String userId;
    @NotBlank
    public String vehicleRegistrationNumber;
    @NotBlank
    public String vehicleMake;
    @NotBlank
    public String vehicleModel;

    public boolean requireManualReview = false;
}
