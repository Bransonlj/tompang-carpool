package com.tompang.carpool.driver_service.dto.verification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DriverVerificationJobDto {

    public final String driverRegistrationId;
    public final String registrationNumber;
    public final String imageKey;

    @JsonCreator
    public DriverVerificationJobDto(
        @JsonProperty("driverRegistrationId") String driverRegistrationId,
        @JsonProperty("registrationNumber") String registrationNumber,
        @JsonProperty("imageKey") String imageKey
    ) {
        this.driverRegistrationId = driverRegistrationId;
        this.registrationNumber = registrationNumber;
        this.imageKey = imageKey;
    }
}
