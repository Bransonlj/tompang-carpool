package com.tompang.carpool.auth_service.exception;

public class UsernameAlreadyExistsException extends UniqueConstraintException {
    public UsernameAlreadyExistsException() {
        super("Username already exists");
    }
}
