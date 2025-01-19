package com.quisin.order.service.dto;

import com.quisin.order.service.model.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String specialInstructions;
    private OrderItemStatus status;
} 