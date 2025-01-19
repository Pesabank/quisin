package com.quisin.order.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    @NotNull
    private Long menuItemId;

    @NotNull
    @Min(1)
    private Integer quantity;

    private String specialInstructions;
} 