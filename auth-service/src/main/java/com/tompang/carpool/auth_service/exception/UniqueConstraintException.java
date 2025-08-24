package com.tompang.carpool.auth_service.exception;

public class UniqueConstraintException extends RuntimeException {
    public UniqueConstraintException(String message) {
        super(message);
    }
}
