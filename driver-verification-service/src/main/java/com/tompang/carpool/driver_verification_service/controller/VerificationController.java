package com.tompang.carpool.driver_verification_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.driver_verification_service.dto.VerificationResult;
import com.tompang.carpool.driver_verification_service.service.VerificationService;

@RestController
@RequestMapping("/api/driver-verification")
public class VerificationController {

    private final VerificationService service;

    public VerificationController(
        VerificationService service
    ) {
        this.service = service;
    }

    @PostMapping("verify")
    public ResponseEntity<VerificationResult> verify(
        @RequestParam("image") String image,
        @RequestParam("registrationNumber") String registrationNumber
    ) {
        VerificationResult result = service.verifyRegistrationNumber(image, registrationNumber);
        
        return ResponseEntity.ok().body(result);
    }
}
