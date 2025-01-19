package com.quisin.order.service.exception;

public class MenuValidationException extends RuntimeException {
    public MenuValidationException(String message) {
        super(message);
    }

    public MenuValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 