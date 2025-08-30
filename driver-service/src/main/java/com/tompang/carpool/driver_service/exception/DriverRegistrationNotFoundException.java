package com.tompang.carpool.driver_service.exception;

public class DriverRegistrationNotFoundException extends ResourceNotFoundException {

    public DriverRegistrationNotFoundException(String driverRegistrationId) {
        super(String.format("Driver Registration not found, id: %s", driverRegistrationId));
    }
}
