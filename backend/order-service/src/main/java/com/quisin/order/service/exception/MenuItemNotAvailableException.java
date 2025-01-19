package com.quisin.order.service.exception;

public class MenuItemNotAvailableException extends RuntimeException {
    public MenuItemNotAvailableException(String message) {
        super(message);
    }

    public MenuItemNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
} 