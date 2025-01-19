package com.quisin.order.controller

import com.quisin.order.domain.OrderStatus
import com.quisin.order.dto.*
import com.quisin.order.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Management", description = "Operations for managing restaurant orders")
class OrderController(
    private val orderService: OrderService
) {
    @PostMapping
    @Operation(summary = "Create a new order", description = "Place a new order for a restaurant")
    fun createOrder(
        @Valid @RequestBody request: CreateOrderRequest
    ): ResponseEntity<OrderResponse> {
        val order = orderService.createOrder(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(order)
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve detailed information about a specific order")
    fun getOrderById(
        @PathVariable orderId: UUID
    ): ResponseEntity<OrderResponse> {
        val order = orderService.getOrderById(orderId)
        return ResponseEntity.ok(order)
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get orders by restaurant", description = "Retrieve all orders for a specific restaurant")
    fun getOrdersByRestaurant(
        @PathVariable restaurantId: UUID
    ): ResponseEntity<List<OrderSummaryResponse>> {
        val orders = orderService.getOrdersByRestaurant(restaurantId)
        return ResponseEntity.ok(orders)
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer", description = "Retrieve all orders for a specific customer")
    fun getOrdersByCustomer(
        @PathVariable customerId: UUID
    ): ResponseEntity<List<OrderSummaryResponse>> {
        val orders = orderService.getOrdersByCustomer(customerId)
        return ResponseEntity.ok(orders)
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Change the status of an existing order")
    fun updateOrderStatus(
        @PathVariable orderId: UUID,
        @Valid @RequestBody request: UpdateOrderStatusRequest
    ): ResponseEntity<OrderResponse> {
        val order = orderService.updateOrderStatus(orderId, request)
        return ResponseEntity.ok(order)
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieve all orders with a specific status")
    fun getOrdersByStatus(
        @PathVariable status: OrderStatus
    ): ResponseEntity<List<OrderSummaryResponse>> {
        val orders = orderService.getOrdersByStatus(status)
        return ResponseEntity.ok(orders)
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get orders by date range", description = "Retrieve orders within a specific time period")
    fun getOrdersByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<List<OrderSummaryResponse>> {
        val orders = orderService.getOrdersByDateRange(startDate, endDate)
        return ResponseEntity.ok(orders)
    }

    @PostMapping("/group")
    @Operation(summary = "Create a group order", description = "Place a new group order for multiple customers")
    fun createGroupOrder(
        @Valid @RequestBody request: GroupOrderRequest
    ): ResponseEntity<GroupOrderResponse> {
        val groupOrder = orderService.createGroupOrder(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(groupOrder)
    }

    @GetMapping("/group/{groupOrderId}")
    @Operation(summary = "Get group order details", description = "Retrieve details of a specific group order")
    fun getGroupOrder(
        @PathVariable groupOrderId: UUID
    ): ResponseEntity<GroupOrderResponse> {
        val groupOrder = orderService.getGroupOrder(groupOrderId)
        return ResponseEntity.ok(groupOrder)
    }

    @PatchMapping("/group/{groupOrderId}/status")
    @Operation(summary = "Update group order status", description = "Change the status of a group order")
    fun updateGroupOrderStatus(
        @PathVariable groupOrderId: UUID,
        @Valid @RequestBody request: UpdateOrderStatusRequest
    ): ResponseEntity<GroupOrderResponse> {
        val groupOrder = orderService.updateGroupOrderStatus(groupOrderId, request.status)
        return ResponseEntity.ok(groupOrder)
    }

    @GetMapping("/group")
    @Operation(summary = "Get group orders", description = "Retrieve all group orders")
    fun getGroupOrders(): ResponseEntity<List<GroupOrderResponse>> {
        val groupOrders = orderService.getGroupOrders()
        return ResponseEntity.ok(groupOrders)
    }

    @PostMapping("/assign-waiter")
    @Operation(summary = "Assign a waiter to an order", description = "Assign a waiter to an existing order")
    fun assignWaiterToOrder(
        @Valid @RequestBody request: WaiterAssignmentRequest
    ): ResponseEntity<OrderResponse> {
        val updatedOrder = orderService.assignWaiterToOrder(request)
        return ResponseEntity.ok(updatedOrder)
    }

    @PostMapping("/group/{groupOrderId}/assign-waiter")
    @Operation(summary = "Assign a waiter to a group order", description = "Assign a waiter to an existing group order")
    fun assignWaiterToGroupOrder(
        @PathVariable groupOrderId: UUID,
        @RequestParam waiterId: UUID
    ): ResponseEntity<GroupOrderResponse> {
        val updatedGroupOrder = orderService.assignWaiterToGroupOrder(groupOrderId, waiterId)
        return ResponseEntity.ok(updatedGroupOrder)
    }
}
