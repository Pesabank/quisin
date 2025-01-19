package com.quisin.order.service.event;

import com.quisin.order.service.model.OrderItemStatus;
import com.quisin.order.service.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long restaurantId;
    private Long customerId;
    private Long waiterId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemEvent> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemEvent {
        private Long menuItemId;
        private String menuItemName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private OrderItemStatus status;
    }
} 