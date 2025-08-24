package com.tompang.carpool.auth_service.exception;

public class EmailAlreadyExistsException extends UniqueConstraintException {
    public EmailAlreadyExistsException() {
        super("Email already exists");
    }
}
