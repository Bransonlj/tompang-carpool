package com.tompang.carpool.carpool_service.command.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.carpool_service.command.command.carpool.CreateCarpoolCommand;
import com.tompang.carpool.carpool_service.command.service.CarpoolCommandHandler;

@RestController
@RequestMapping("/command/carpool")
public class CarpoolCommandController {

    private final CarpoolCommandHandler carpoolCommandHandler;

    public CarpoolCommandController(CarpoolCommandHandler carpoolCommandHandler) {
        this.carpoolCommandHandler = carpoolCommandHandler;
    }

    @PostMapping("create")
    public ResponseEntity<Void> create(
        @RequestBody CreateCarpoolCommand command
    ) {
        String carpoolId = this.carpoolCommandHandler.handleCreateCarpool(command);
        URI location = URI.create("/query/carpool/" + carpoolId);
        return ResponseEntity.created(location).build();
    }

}
