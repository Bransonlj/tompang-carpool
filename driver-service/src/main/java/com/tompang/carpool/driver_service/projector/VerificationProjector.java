package com.tompang.carpool.driver_service.projector;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.driver_service.config.RabbitConfig;
import com.tompang.carpool.driver_service.dto.verification.DriverVerificationResultDto;
import com.tompang.carpool.driver_service.dto.verification.VerificationResult;
import com.tompang.carpool.driver_service.exception.BadRequestException;
import com.tompang.carpool.driver_service.exception.ResourceNotFoundException;
import com.tompang.carpool.driver_service.model.DriverRegistration;
import com.tompang.carpool.driver_service.model.RegistrationStatus;
import com.tompang.carpool.driver_service.repository.DriverRegistrationRepository;
import com.tompang.carpool.driver_service.service.KafkaProducerService;

import jakarta.transaction.Transactional;

@Component
public class VerificationProjector {

    private final DriverRegistrationRepository registrationRepository;
    private final KafkaProducerService kafkaProducerService;

    public VerificationProjector(DriverRegistrationRepository registrationRepository, KafkaProducerService kafkaProducerService) {
        this.registrationRepository = registrationRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    @RabbitListener(queues = RabbitConfig.DRIVER_VERIFICATION_RESULT_QUEUE)
    public void handleVerificationCompleted(DriverVerificationResultDto dto) {
        DriverRegistration driver = registrationRepository.findById(dto.driverRegistrationId)
                .orElseThrow(() -> new ResourceNotFoundException("driverRegistration not found, id:" + dto.driverRegistrationId));
        if (!driver.getRegistrationStatus().equals(RegistrationStatus.PENDING)) {
            throw new BadRequestException("Cannot auto update registration status that is not 'PENDING'");
        }
                
        // update registration status
        if (dto.result.equals(VerificationResult.VALID)) {
            // update old success registration to inactive
            List<DriverRegistration> registrations = registrationRepository.findAllByUserIdAndRegistrationStatus(driver.getUserId(), RegistrationStatus.SUCCESS);
            for (DriverRegistration registration : registrations) {
                if (registration.getRegistrationStatus().equals(RegistrationStatus.SUCCESS) 
                        && !registration.getId().equals(driver.getId())) {
                    registration.setRegistrationStatus(RegistrationStatus.INACTIVE);
                    registrationRepository.save(registration);
                }
            }

            driver.setRegistrationStatus(RegistrationStatus.SUCCESS);
        } else if (dto.result.equals(VerificationResult.UNSURE)) {
            driver.setRegistrationStatus(RegistrationStatus.PENDING_MANUAL_REVIEW);
        } else if (dto.result.equals(VerificationResult.INVALID)) {
            driver.setRegistrationStatus(RegistrationStatus.FAILED);
        }

        registrationRepository.save(driver);

        // produce kafka event
        if (dto.result.equals(VerificationResult.VALID)) {
            kafkaProducerService.produceDriverRegistrationApproved(driver.getId(), driver.getUserId());
        } else if (dto.result.equals(VerificationResult.INVALID)) {
            kafkaProducerService.produceDriverRegistrationRejected(driver.getId(), driver.getUserId());
        }
    }
}
