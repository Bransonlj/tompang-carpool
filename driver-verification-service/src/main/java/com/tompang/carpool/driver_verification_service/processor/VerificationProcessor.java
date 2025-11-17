package com.tompang.carpool.driver_verification_service.processor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
    private final String bucketName;

    public VerificationProcessor(
        VerificationService service, 
        RabbitTemplate rabbitTemplate, 
        @Qualifier("s3BucketName") String bucketName
    ) {
        this.service = service;
        this.rabbitTemplate = rabbitTemplate;
        this.bucketName = bucketName;
    }

    @RabbitListener(queues = RabbitConfig.DRIVER_VERIFICATION_JOB_QUEUE)
    public void processReverseGeocodeJob(DriverVerificationJobDto job) {
        VerificationResult result = service.verifyRegistrationNumber(S3Object.builder().name(job.imageKey).bucket(bucketName).build(), job.registrationNumber);
        rabbitTemplate.convertAndSend(RabbitConfig.DRIVER_VERIFICATION_RESULT_QUEUE, new DriverVerificationResultDto(job.driverRegistrationId, result));
    }
}
