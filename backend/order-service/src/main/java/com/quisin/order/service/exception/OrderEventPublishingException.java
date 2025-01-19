package com.quisin.order.service.exception;

public class OrderEventPublishingException extends RuntimeException {
    public OrderEventPublishingException(String message) {
        super(message);
    }

    public OrderEventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
} 