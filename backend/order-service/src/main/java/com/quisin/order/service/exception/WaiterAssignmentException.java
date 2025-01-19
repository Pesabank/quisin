package com.quisin.order.service.exception;

public class WaiterAssignmentException extends RuntimeException {
    public WaiterAssignmentException(String message) {
        super(message);
    }

    public WaiterAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
} 