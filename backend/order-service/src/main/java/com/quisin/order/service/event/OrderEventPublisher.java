package com.quisin.order.service.event;

import com.quisin.order.service.exception.OrderEventPublishingException;
import com.quisin.order.service.model.Order;
import com.quisin.order.service.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {
    private final StreamBridge streamBridge;
    private static final String OUTPUT_BINDING = "orderEvents-out-0";

    public void publishOrderEvent(Order order) {
        OrderEvent event = OrderEvent.builder()
                .orderId(order.getId())
                .restaurantId(order.getRestaurantId())
                .customerId(order.getCustomerId())
                .waiterId(order.getWaiterId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems().stream()
                        .map(item -> OrderEvent.OrderItemEvent.builder()
                                .menuItemId(item.getMenuItemId())
                                .menuItemName(item.getMenuItemName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .subtotal(item.getSubtotal())
                                .status(item.getStatus())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        try {
            log.info("Publishing order event for order ID: {}", order.getId());
            boolean sent = streamBridge.send(OUTPUT_BINDING, event);
            if (sent) {
                log.info("Successfully published order event for order ID: {}", order.getId());
            } else {
                log.error("Failed to publish order event for order ID: {}", order.getId());
                throw new OrderEventPublishingException("Failed to publish order event for order ID: " + order.getId());
            }
        } catch (Exception e) {
            log.error("Error publishing order event for order ID: {}", order.getId(), e);
            throw new OrderEventPublishingException("Error publishing order event: " + e.getMessage());
        }
    }
} 