package com.tompang.carpool.carpool_service.command.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.carpool_service.command.command.carpool.AcceptCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.command.carpool.DeclineCarpoolRequestCommand;
import com.tompang.carpool.carpool_service.command.service.CarpoolCommandHandler;

@RestController
@RequestMapping("api/carpool/command")
public class CarpoolCommandController {

    private final CarpoolCommandHandler carpoolCommandHandler;

    public CarpoolCommandController(CarpoolCommandHandler carpoolCommandHandler) {
        this.carpoolCommandHandler = carpoolCommandHandler;
    }

    @GetMapping("health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().body("CarpoolService carpool-command-controller is healthy.");
    }

    @PostMapping("create")
    public ResponseEntity<Void> create(
        @RequestBody CreateCarpoolCommand command
    ) {
        String carpoolId = this.carpoolCommandHandler.handleCreateCarpool(command);
        URI location = URI.create("/api/carpool/query/" + carpoolId);
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/request/accept")
    public ResponseEntity<Void> acceptRequest(
        @RequestBody AcceptCarpoolRequestCommand command
    ) {
        this.carpoolCommandHandler.handleAcceptCarpoolRequest(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request/decline")
    public ResponseEntity<Void> declineRequest(
        @RequestBody DeclineCarpoolRequestCommand command
    ) {
        this.carpoolCommandHandler.handleDeclineCarpoolRequest(command);
        return ResponseEntity.ok().build();
    }

}
