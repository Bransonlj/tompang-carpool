package com.tompang.carpool.carpool_service.command.domain.exception;

public class DomainException extends RuntimeException {
  public DomainException(String message) {
    super(message);
  }
}
