package com.tompang.carpool.driver_verification_service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DriverVerificationResultDto {
    public final String driverRegistrationId;
    public final VerificationResult result;

    @JsonCreator
    public DriverVerificationResultDto(
        @JsonProperty("driverRegistrationId") String driverRegistrationId,
        @JsonProperty("result") VerificationResult result
    ) {
        this.driverRegistrationId = driverRegistrationId;
        this.result = result;
    }
}
