package com.quisin.order.service.dto;

import com.quisin.order.service.model.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotNull
    private Long restaurantId;

    @NotNull
    private Long customerId;

    private Long tableId;

    @NotNull
    private OrderType type;

    @Size(min = 1)
    private List<OrderItemRequest> items;

    private String specialInstructions;

    private Long groupOrderId;

    private Boolean isWaiterAssignmentRequested;
} 