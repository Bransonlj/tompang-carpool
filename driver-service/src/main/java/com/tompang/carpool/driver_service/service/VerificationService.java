package com.tompang.carpool.driver_service.service;

import java.io.IOException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tompang.carpool.driver_service.config.RabbitConfig;
import com.tompang.carpool.driver_service.dto.RegisterDriverRequestDto;
import com.tompang.carpool.driver_service.dto.verification.DriverVerificationJobDto;

@Service
public class VerificationService {

    private final RabbitTemplate rabbitTemplate;
    private final DriverRegistrationService driverService;
    private final S3Service s3Service;

    public VerificationService(RabbitTemplate rabbitTemplate, DriverRegistrationService driverService, S3Service s3Service) {
        this.rabbitTemplate = rabbitTemplate;
        this.driverService = driverService;
        this.s3Service = s3Service;
    }

    /**
     * Creates driver, uploads image to S3, create verification job.
     * @param dto
     * @param file
     * @return The id of the registration.
     */
    public String registerAndVerifyDriver(RegisterDriverRequestDto dto, MultipartFile file) {
        // create driver
        String driverRegistrationId = driverService.registerDriver(dto);
        // upload image
        try {
            s3Service.uploadFile(new S3Service.Key(S3Service.DRIVER_FOLDER, driverRegistrationId), file);
        } catch (IOException exception) {
            driverService.failDriverRegistration(driverRegistrationId, "Failed to upload image");
            return driverRegistrationId;
        } catch (Exception exception) {
            // rollback any other exception
            driverService.deleteDriverRegistration(driverRegistrationId);
            throw exception;
        }

        rabbitTemplate.convertAndSend(RabbitConfig.DRIVER_VERIFICATION_JOB_QUEUE, new DriverVerificationJobDto(
                driverRegistrationId, dto.getVehicleRegistrationNumber(),
                S3Service.Key.builder()
                        .dir(S3Service.DRIVER_FOLDER)
                        .id(driverRegistrationId)
                        .build().toString()
        ));

        return driverRegistrationId;
    }

}
