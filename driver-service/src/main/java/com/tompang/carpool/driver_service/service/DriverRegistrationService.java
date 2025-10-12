package com.tompang.carpool.driver_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tompang.carpool.driver_service.dto.RegisterDriverRequestDto;
import com.tompang.carpool.driver_service.exception.ResourceNotFoundException;
import com.tompang.carpool.driver_service.model.DriverRegistration;
import com.tompang.carpool.driver_service.model.RegistrationStatus;
import com.tompang.carpool.driver_service.repository.DriverRegistrationRepository;

@Service
public class DriverRegistrationService {

    private final DriverRegistrationRepository repository;

    public DriverRegistrationService(DriverRegistrationRepository repository) {
        this.repository = repository;
    }

    public DriverRegistration getDriverById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver registration not found"));
    }

    public List<DriverRegistration> getRegistrationByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }

    public String registerDriver(RegisterDriverRequestDto dto) {
        DriverRegistration driver = DriverRegistration.builder()
            .userId(dto.getUserId())
            .vehicleRegistrationNumber(dto.getVehicleRegistrationNumber())
            .vehicleMake(dto.getVehicleMake())
            .vehicleModel(dto.getVehicleModel())
            .createdAt(LocalDateTime.now())
            .build();

        if (dto.isRequireManualReview()) {
            driver.setRegistrationStatus(RegistrationStatus.PENDING_MANUAL_REVIEW);
        }
        
        DriverRegistration createdDriver = repository.save(driver);
        return createdDriver.getId();
    }

    public void deleteDriverRegistration(String id) {
        repository.deleteById(id);
    }

}
