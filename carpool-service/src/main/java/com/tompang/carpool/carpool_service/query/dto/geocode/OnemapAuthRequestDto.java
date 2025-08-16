package com.tompang.carpool.carpool_service.query.dto.geocode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OnemapAuthRequestDto {

    public final String email;
    public final String password;

    @JsonCreator
    public OnemapAuthRequestDto(
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
    ) {
        this.email = email;
        this.password = password;
    }
}
