package com.tompang.carpool.driver_service.dto;

import lombok.Data;

@Data
public class RegisterDriverRequestDto {
    public String userId;
    public String vehicleRegistrationNumber;
    public String vehicleMake;
    public String vehicleModel;
}
