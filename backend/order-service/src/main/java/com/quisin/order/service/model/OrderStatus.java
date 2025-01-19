package com.quisin.order.service.model;

public enum OrderStatus {
    PENDING,           // Order created but not confirmed
    CONFIRMED,         // Order confirmed and being prepared
    IN_PREPARATION,    // Kitchen is preparing the order
    READY,            // Order is ready for pickup/delivery/serving
    DELIVERED,        // Order has been delivered to the customer
    COMPLETED,        // Order has been completed successfully
    CANCELLED,        // Order has been cancelled
    REFUNDED          // Order has been refunded
} 