package com.tompang.carpool.carpool_service.command.command.carpool;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public class DeclineCarpoolRequestCommand {
    public final String carpoolId;
    public final String requestId;

    @JsonCreator
    public DeclineCarpoolRequestCommand(
        @JsonProperty("carpoolId") String carpoolId,
        @JsonProperty("requestId") String requestId
    ) {
        this.carpoolId = carpoolId;
        this.requestId = requestId;
    }
}
