package com.tompang.carpool.carpool_service.command.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.carpool_service.command.command.ride_request.CreateRideRequestCommand;
import com.tompang.carpool.carpool_service.command.service.RideRequestCommandHandler;

@RestController
@RequestMapping("api/ride-request/command")
public class RideRequestCommandController {

    private final RideRequestCommandHandler commandHandler;

    public RideRequestCommandController(RideRequestCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(
        @RequestBody CreateRideRequestCommand command
    ) {
        String requestId = this.commandHandler.handleCreateRideRequest(command);
        URI location = URI.create("/api/ride-request/query/" + requestId);
        return ResponseEntity.created(location).build();
    }

}
