package com.tompang.carpool.carpool_service.query.dto.geocode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OnemapAuthResponseDto {

    public final String accessToken;
    public final String expiryTimestamp;

    @JsonCreator
    public OnemapAuthResponseDto(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expiry_timestamp") String expiryTimestamp
    ) {
        this.accessToken = accessToken;
        this.expiryTimestamp = expiryTimestamp;
    }

}
