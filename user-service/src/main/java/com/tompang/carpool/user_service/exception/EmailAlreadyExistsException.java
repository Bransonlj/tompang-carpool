package com.tompang.carpool.user_service.exception;

public class EmailAlreadyExistsException extends UniqueConstraintException {
    public EmailAlreadyExistsException() {
        super("Email already exists");
    }
}
