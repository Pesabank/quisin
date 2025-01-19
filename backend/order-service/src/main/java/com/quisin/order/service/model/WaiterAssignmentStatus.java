package com.quisin.order.service.model;

public enum WaiterAssignmentStatus {
    REQUESTED,  // Initial state when waiter assignment is requested
    PENDING,    // Waiter assignment requested but not yet assigned
    ASSIGNED,   // Waiter has been assigned to the order
    DECLINED,   // Waiter declined the order assignment
    COMPLETED   // Order served by the assigned waiter
} 