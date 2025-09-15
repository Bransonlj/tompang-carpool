package com.tompang.carpool.carpool_service.command.command.carpool;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.EqualsAndHashCode;

/**
 * Command to invalidate a request to a carpool.
 */
@EqualsAndHashCode
@Builder
public class InvalidateCarpoolRequestCommand {
    public final String carpoolId;
    public final String requestId;
    public final String reason;

    @JsonCreator
    public InvalidateCarpoolRequestCommand (
        @JsonProperty("carpoolId") String carpoolId,
        @JsonProperty("requestId") String requestId,
        @JsonProperty("reason") String reason
    ) {
        this.carpoolId = carpoolId;
        this.requestId = requestId;
        this.reason = reason;
    }
}
