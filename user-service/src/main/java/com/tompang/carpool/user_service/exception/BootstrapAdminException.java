package com.tompang.carpool.user_service.exception;

public class BootstrapAdminException extends RuntimeException {

    public final int level;

    /**
     * 
     * @param message
     * @param level 0=debug, 1=info, 2=warning, 3=error
     */
    public BootstrapAdminException(String message, int level) {
        super(message);
        this.level = level;
    }
}
