package com.tompang.carpool.driver_verification_service.processor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.tompang.carpool.driver_verification_service.config.AwsConfig;
import com.tompang.carpool.driver_verification_service.config.RabbitConfig;
import com.tompang.carpool.driver_verification_service.dto.DriverVerificationJobDto;
import com.tompang.carpool.driver_verification_service.dto.DriverVerificationResultDto;
import com.tompang.carpool.driver_verification_service.dto.VerificationResult;
import com.tompang.carpool.driver_verification_service.service.VerificationService;

import software.amazon.awssdk.services.rekognition.model.S3Object;

@Component
public class VerificationProcessor {

    private final VerificationService service;
    private final RabbitTemplate rabbitTemplate;

    public VerificationProcessor(VerificationService service, RabbitTemplate rabbitTemplate) {
        this.service = service;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.DRIVER_VERIFICATION_JOB_QUEUE)
    public void processReverseGeocodeJob(DriverVerificationJobDto job) {
        VerificationResult result = service.verifyRegistrationNumber(S3Object.builder().name(job.imageKey).bucket(AwsConfig.BUCKET_NAME).build(), job.registrationNumber);
        rabbitTemplate.convertAndSend(RabbitConfig.DRIVER_VERIFICATION_RESULT_QUEUE, new DriverVerificationResultDto(job.driverRegistrationId, result));
    }
}
