package com.quisin.order.service.exception;

public class RestaurantMenuItemMismatchException extends RuntimeException {
    public RestaurantMenuItemMismatchException(String message) {
        super(message);
    }

    public RestaurantMenuItemMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
} 