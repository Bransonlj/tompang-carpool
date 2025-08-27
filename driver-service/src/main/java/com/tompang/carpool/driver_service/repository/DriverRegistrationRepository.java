package com.tompang.carpool.driver_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tompang.carpool.driver_service.model.DriverRegistration;

public interface DriverRegistrationRepository extends JpaRepository<DriverRegistration, String> {

}
