package com.tompang.carpool.driver_service.controller;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tompang.carpool.driver_service.dto.DriverRegistrationResponseDto;
import com.tompang.carpool.driver_service.dto.RegisterDriverRequestDto;
import com.tompang.carpool.driver_service.exception.BadRequestException;
import com.tompang.carpool.driver_service.model.DriverRegistration;
import com.tompang.carpool.driver_service.service.DriverRegistrationService;
import com.tompang.carpool.driver_service.service.S3Service;
import com.tompang.carpool.driver_service.service.VerificationService;

@RestController
@RequestMapping("/api/driver")
public class DriverController {
    private final S3Service s3Service;
    private final DriverRegistrationService driverService;
    private final VerificationService verificationService;

    public DriverController(S3Service s3Service, DriverRegistrationService driverService, VerificationService verificationService) {
        this.s3Service = s3Service;
        this.driverService = driverService;
        this.verificationService = verificationService;
    }

    @GetMapping("{id}")
    public ResponseEntity<DriverRegistrationResponseDto> getDriverRegistrationById(
        @PathVariable String id
    ) {
        DriverRegistration driver = driverService.getDriverById(id);
        DriverRegistrationResponseDto dto = DriverRegistrationResponseDto.fromEntity(driver);
        String signedImageUrl = s3Service.getFileUrl(S3Service.Key.builder()
                .dir(S3Service.DRIVER_FOLDER)
                .id(id)
                .build());
        dto.setSignedImageUrl(signedImageUrl);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerDriver(
            @RequestPart("image") MultipartFile file,
            @RequestPart("data") RegisterDriverRequestDto dto) throws Exception {

        // Check file is not empty
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || 
            !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            throw new BadRequestException("Only JPEG and PNG files are allowed");
        }

        String driverRegistrationId = verificationService.registerAndVerifyDriver(dto, file);

        URI location = URI.create("/api/driver/" + driverRegistrationId);
        return ResponseEntity.created(location).build();
    }
}
