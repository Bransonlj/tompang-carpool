package com.tompang.carpool.carpool_service.command.domain.exception;

public class CarpoolAndRideRequestAlreadyAssignedException extends DomainException {
  public CarpoolAndRideRequestAlreadyAssignedException(String requestId) {
    super("RideRequest-" + requestId + "is already assigned to a carpool");
  }

  public CarpoolAndRideRequestAlreadyAssignedException(String requestId, String carpoolId) {
    super("RideRequest-" + requestId + "is already assigned to carpool-" + carpoolId);
  }
}
