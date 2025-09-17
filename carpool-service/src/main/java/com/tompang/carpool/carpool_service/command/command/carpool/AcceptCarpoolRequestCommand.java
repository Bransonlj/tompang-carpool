package com.tompang.carpool.carpool_service.command.command.carpool;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class AcceptCarpoolRequestCommand {
    public final String carpoolId;
    public final String requestId;

    @JsonCreator
    public AcceptCarpoolRequestCommand(
        @JsonProperty("carpoolId") String carpoolId,
        @JsonProperty("requestId") String requestId
    ) {
        this.carpoolId = carpoolId;
        this.requestId = requestId;
    }
}
