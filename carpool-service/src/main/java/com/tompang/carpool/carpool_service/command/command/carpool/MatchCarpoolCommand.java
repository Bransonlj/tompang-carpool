package com.tompang.carpool.carpool_service.command.command.carpool;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchCarpoolCommand {
    public final String carpoolId;
    public final String requestId;
}
