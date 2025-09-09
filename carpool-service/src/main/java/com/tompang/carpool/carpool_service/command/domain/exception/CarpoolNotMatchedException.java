package com.tompang.carpool.carpool_service.command.domain.exception;

public class CarpoolNotMatchedException extends DomainException {
  public CarpoolNotMatchedException(String requestId, String carpoolId) {
    super(String.format("Carpool %s is not matched with RideRequest %s", carpoolId, requestId));
  }
}
