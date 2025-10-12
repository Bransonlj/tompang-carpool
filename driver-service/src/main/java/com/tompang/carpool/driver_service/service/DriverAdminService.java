package com.tompang.carpool.driver_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tompang.carpool.driver_service.exception.BadRequestException;
import com.tompang.carpool.driver_service.exception.DriverRegistrationNotFoundException;
import com.tompang.carpool.driver_service.model.DriverRegistration;
import com.tompang.carpool.driver_service.model.ManualReview;
import com.tompang.carpool.driver_service.model.RegistrationStatus;
import com.tompang.carpool.driver_service.repository.DriverRegistrationRepository;

import jakarta.transaction.Transactional;

@Service
public class DriverAdminService {
    private final DriverRegistrationRepository registrationRepository;
    private final KafkaProducerService kafkaProducerService;

    public DriverAdminService(DriverRegistrationRepository registrationRepository, KafkaProducerService kafkaProducerService) {
        this.registrationRepository = registrationRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<DriverRegistration> getAllPendingManualReview() {
        return registrationRepository.findAllByRegistrationStatus(RegistrationStatus.PENDING_MANUAL_REVIEW);
    }

    @Transactional
    public void manuallyApproveDriverRegistration(String driverRegistrationId, String adminId) {
        DriverRegistration registration = registrationRepository.findById(driverRegistrationId).orElseThrow(() -> new DriverRegistrationNotFoundException(driverRegistrationId));
        if (!registration.getRegistrationStatus().equals(RegistrationStatus.PENDING_MANUAL_REVIEW)) {
            throw new BadRequestException("Cannot manually accept driver registration that is not pending manual review. Status: " + registration.getRegistrationStatus());
        }

        // update old success registration to inactive
        List<DriverRegistration> registrations = registrationRepository.findAllByUserIdAndRegistrationStatus(registration.getUserId(), RegistrationStatus.SUCCESS);
        for (DriverRegistration oldRegistration : registrations) {
            if (oldRegistration.getRegistrationStatus().equals(RegistrationStatus.SUCCESS) 
                    && !oldRegistration.getId().equals(registration.getId())) {
                oldRegistration.setRegistrationStatus(RegistrationStatus.INACTIVE);
                registrationRepository.save(oldRegistration);
            }
        }

        registration.setRegistrationStatus(RegistrationStatus.SUCCESS);
        registration.setManualReview(ManualReview.builder()
            .reviewerId(adminId)
            .isApproved(true)
            .createdAt(LocalDateTime.now())
            .build()); // No need to save ManualReview because CascadeType = All
        
        registrationRepository.save(registration);
        kafkaProducerService.produceDriverRegistrationApproved(driverRegistrationId, registration.getUserId());
    }

    @Transactional
    public void manuallyRejectDriverRegistration(String driverRegistrationId, String adminId, String reason) {
        DriverRegistration registration = registrationRepository.findById(driverRegistrationId).orElseThrow(() -> new DriverRegistrationNotFoundException(driverRegistrationId));
        if (!registration.getRegistrationStatus().equals(RegistrationStatus.PENDING_MANUAL_REVIEW)) {
            throw new BadRequestException("Cannot manually accept driver registration that is not pending manual review. Status: " + registration.getRegistrationStatus());
        }

        registration.setRegistrationStatus(RegistrationStatus.FAILED);
        registration.setManualReview(ManualReview.builder()
            .reviewerId(adminId)
            .isApproved(false)
            .failReason(reason)
            .createdAt(LocalDateTime.now())
            .build()); // No need to save ManualReview because CascadeType = All
        
        registrationRepository.save(registration);
        kafkaProducerService.produceDriverRegistrationRejected(driverRegistrationId, registration.getUserId());
    }

}
