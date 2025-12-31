package com.tompang.carpool.carpool_service.command.domain.exception;

public class CarpoolAndRideRequestAlreadyMatchedException extends DomainException {
  public CarpoolAndRideRequestAlreadyMatchedException(String requestId, String carpoolId) {
    super("RideRequest-" + requestId + "is already matched to carpool-" + carpoolId);
  }
}
