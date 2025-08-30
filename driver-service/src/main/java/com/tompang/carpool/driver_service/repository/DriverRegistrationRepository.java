package com.tompang.carpool.driver_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tompang.carpool.driver_service.model.DriverRegistration;
import com.tompang.carpool.driver_service.model.RegistrationStatus;

public interface DriverRegistrationRepository extends JpaRepository<DriverRegistration, String> {

    List<DriverRegistration> findAllByRegistrationStatus(RegistrationStatus registrationStatus);
}
