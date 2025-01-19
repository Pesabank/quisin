package com.quisin.qrcode.exception;

public class TableAlreadyExistsException extends RuntimeException {
    public TableAlreadyExistsException(String message) {
        super(message);
    }
} 