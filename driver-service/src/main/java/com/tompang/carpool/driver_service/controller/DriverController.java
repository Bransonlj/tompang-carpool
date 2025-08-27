package com.tompang.carpool.driver_service.controller;

import java.io.IOException;
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

@RestController
@RequestMapping("/api/driver")
public class DriverController {
    private final S3Service s3Service;
    private final DriverRegistrationService driverService;

    public DriverController(S3Service s3Service, DriverRegistrationService driverService) {
        this.s3Service = s3Service;
        this.driverService = driverService;
    }

    @GetMapping("{id}")
    public ResponseEntity<DriverRegistrationResponseDto> getDriverRegistrationById(
        @PathVariable String id
    ) {
        DriverRegistration driver = driverService.getDriverById(id);
        DriverRegistrationResponseDto dto = DriverRegistrationResponseDto.fromEntity(driver);
        String signedImageUrl = s3Service.getFileUrl(id);
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

        // create driver
        String driverRegistrationId = driverService.registerDriver(dto);
        // upload image
        try {
            s3Service.uploadFile(driverRegistrationId, file);
        } catch (IOException exception) {
            driverService.failDriverRegistration(driverRegistrationId, "Failed to upload image");
        } catch (Exception exception) {
            // rollback any other exception
            driverService.deleteDriverRegistration(driverRegistrationId);
            throw exception;
        }

        URI location = URI.create("/api/driver/" + driverRegistrationId);
        return ResponseEntity.created(location).build();
    }
}
