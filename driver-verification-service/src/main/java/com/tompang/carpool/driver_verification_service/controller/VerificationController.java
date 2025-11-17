package com.tompang.carpool.driver_verification_service.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.driver_verification_service.dto.VerificationResult;
import com.tompang.carpool.driver_verification_service.service.VerificationService;

import software.amazon.awssdk.services.rekognition.model.S3Object;

@RestController
@RequestMapping("/api/driver-verification")
public class VerificationController {

    private final VerificationService service;
    private final String bucketName;

    public VerificationController(
        VerificationService service, 
        @Qualifier("s3BucketName") String bucketName
    ) {
        this.service = service;
        this.bucketName = bucketName;
    }

    @PostMapping("verify")
    public ResponseEntity<VerificationResult> verify(
        @RequestParam("image") String image,
        @RequestParam("registrationNumber") String registrationNumber
    ) {
        VerificationResult result = service.verifyRegistrationNumber(
            S3Object.builder()
                .name(image)
                .bucket(bucketName)
                .build(), 
            registrationNumber);
        
        return ResponseEntity.ok().body(result);
    }
}
