package com.tompang.carpool.geospatial_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geospatial")
public class Controller {
    @GetMapping("health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().body("Geospatial Service is healthy.");
    }
}
