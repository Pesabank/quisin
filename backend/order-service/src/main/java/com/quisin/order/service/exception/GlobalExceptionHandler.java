package com.quisin.order.service.exception;

import com.quisin.order.service.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        log.error("Order not found: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMenuItemNotFoundException(MenuItemNotFoundException ex) {
        log.error("Menu item not found: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MenuItemNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleMenuItemNotAvailableException(MenuItemNotAvailableException ex) {
        log.error("Menu item not available: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestaurantMenuItemMismatchException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantMenuItemMismatchException(RestaurantMenuItemMismatchException ex) {
        log.error("Restaurant menu item mismatch: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(new ErrorResponse("An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 