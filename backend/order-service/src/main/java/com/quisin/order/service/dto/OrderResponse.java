package com.quisin.order.service.dto;

import com.quisin.order.service.model.OrderStatus;
import com.quisin.order.service.model.OrderType;
import com.quisin.order.service.model.WaiterAssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long restaurantId;
    private Long customerId;
    private Long waiterId;
    private Long tableId;
    private OrderType type;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;
    private String specialInstructions;
    private Long groupOrderId;
    private Boolean isWaiterAssignmentRequested;
    private WaiterAssignmentStatus waiterAssignmentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
} 