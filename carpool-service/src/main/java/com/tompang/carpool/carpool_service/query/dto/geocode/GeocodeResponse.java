package com.tompang.carpool.carpool_service.query.dto.geocode;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeocodeResponse {

    public static final String NIL = "NIL";

    @JsonProperty("GeocodeInfo")
    private List<GeocodeInfo> geocodeInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeocodeInfo {
        @JsonProperty("BUILDINGNAME")
        private String buildingName;

        @JsonProperty("BLOCK")
        private String block;

        @JsonProperty("ROAD")
        private String road;

        @JsonProperty("POSTALCODE")
        private String postalCode;

        @JsonProperty("XCOORD")
        private String xCoord;

        @JsonProperty("YCOORD")
        private String yCoord;

        @JsonProperty("LATITUDE")
        private String latitude;

        @JsonProperty("LONGITUDE")
        private String longitude;
    }
}