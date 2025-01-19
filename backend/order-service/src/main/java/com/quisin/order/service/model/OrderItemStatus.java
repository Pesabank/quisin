package com.quisin.order.service.model;

public enum OrderItemStatus {
    PENDING,           // Item added to order but not confirmed
    CONFIRMED,         // Item confirmed and waiting to be prepared
    IN_PREPARATION,    // Item is being prepared in the kitchen
    READY,            // Item is ready to be served
    SERVED,           // Item has been served to the customer
    CANCELLED         // Item has been cancelled
} 