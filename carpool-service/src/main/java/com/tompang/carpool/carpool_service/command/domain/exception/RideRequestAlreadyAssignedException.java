package com.tompang.carpool.carpool_service.command.domain.exception;

public class RideRequestAlreadyAssignedException extends DomainException {
  public RideRequestAlreadyAssignedException(String id) {
    super("RideRequest" + id + "is already assigned a carpool");
  }
}
