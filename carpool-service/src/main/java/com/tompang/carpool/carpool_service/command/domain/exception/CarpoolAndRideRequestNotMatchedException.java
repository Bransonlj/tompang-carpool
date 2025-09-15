package com.tompang.carpool.carpool_service.command.domain.exception;

public class CarpoolAndRideRequestNotMatchedException extends DomainException {
  public CarpoolAndRideRequestNotMatchedException(String requestId, String carpoolId) {
    super(String.format("Carpool %s is not matched with RideRequest %s", carpoolId, requestId));
  }
}
