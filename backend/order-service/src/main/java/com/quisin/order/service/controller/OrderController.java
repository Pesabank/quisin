package com.quisin.order.service.controller;

import com.quisin.order.service.dto.*;
import com.quisin.order.service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'WAITER', 'ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'WAITER', 'KITCHEN_STAFF', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('WAITER', 'KITCHEN_STAFF', 'ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request));
    }

    @PostMapping("/{orderId}/assign-waiter")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<OrderResponse> assignWaiter(
            @PathVariable Long orderId,
            @Valid @RequestBody AssignWaiterRequest request) {
        return ResponseEntity.ok(orderService.assignWaiter(orderId, request));
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN') and @securityService.isCurrentUserOrAdmin(#customerId)")
    public ResponseEntity<Page<OrderResponse>> getCustomerOrders(
            @PathVariable Long customerId,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getCustomerOrders(customerId, pageable));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER', 'KITCHEN_STAFF')")
    public ResponseEntity<Page<OrderResponse>> getRestaurantOrders(
            @PathVariable Long restaurantId,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getRestaurantOrders(restaurantId, pageable));
    }

    @GetMapping("/waiter/{waiterId}")
    @PreAuthorize("hasRole('WAITER') and @securityService.isCurrentUser(#waiterId)")
    public ResponseEntity<Page<OrderResponse>> getWaiterOrders(
            @PathVariable Long waiterId,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getWaiterOrders(waiterId, pageable));
    }

    @GetMapping("/group/{groupOrderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'WAITER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getGroupOrders(@PathVariable Long groupOrderId) {
        return ResponseEntity.ok(orderService.getGroupOrders(groupOrderId));
    }

    @GetMapping("/restaurant/{restaurantId}/by-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER', 'KITCHEN_STAFF')")
    public ResponseEntity<Page<OrderResponse>> getRestaurantOrdersByStatus(
            @PathVariable Long restaurantId,
            @RequestParam List<String> statuses,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getRestaurantOrdersByStatus(restaurantId, statuses, pageable));
    }

    @GetMapping("/waiter/{waiterId}/by-status")
    @PreAuthorize("hasRole('WAITER') and @securityService.isCurrentUser(#waiterId)")
    public ResponseEntity<Page<OrderResponse>> getWaiterOrdersByStatus(
            @PathVariable Long waiterId,
            @RequestParam List<String> statuses,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getWaiterOrdersByStatus(waiterId, statuses, pageable));
    }

    @GetMapping("/restaurant/{restaurantId}/by-date")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER', 'KITCHEN_STAFF')")
    public ResponseEntity<Page<OrderResponse>> getRestaurantOrdersByDateRange(
            @PathVariable Long restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getRestaurantOrdersByDateRange(restaurantId, startDate, endDate, pageable));
    }
} 