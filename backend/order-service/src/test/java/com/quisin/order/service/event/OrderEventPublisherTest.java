package com.quisin.order.service.event;

import com.quisin.order.service.model.Order;
import com.quisin.order.service.model.OrderItem;
import com.quisin.order.service.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherTest {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private OrderEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<OrderEvent> eventCaptor;

    private static final String OUTPUT_BINDING = "orderEvents-out-0";

    @Test
    void publishOrderEvent_Success() {
        // Arrange
        Order order = createTestOrder();
        when(streamBridge.send(eq(OUTPUT_BINDING), any(OrderEvent.class))).thenReturn(true);

        // Act
        eventPublisher.publishOrderEvent(order);

        // Assert
        verify(streamBridge).send(eq(OUTPUT_BINDING), eventCaptor.capture());
        OrderEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent)
                .satisfies(event -> {
                    assertThat(event.getOrderId()).isEqualTo(order.getId());
                    assertThat(event.getRestaurantId()).isEqualTo(order.getRestaurantId());
                    assertThat(event.getCustomerId()).isEqualTo(order.getCustomerId());
                    assertThat(event.getStatus()).isEqualTo(order.getStatus());
                    assertThat(event.getTotalAmount()).isEqualTo(order.getTotalAmount());
                    assertThat(event.getItems()).hasSize(1);
                });
    }

    private Order createTestOrder() {
        OrderItem item = OrderItem.builder()
                .id(1L)
                .menuItemId(1L)
                .menuItemName("Test Item")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(10))
                .subtotal(BigDecimal.valueOf(20))
                .build();

        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        return Order.builder()
                .id(1L)
                .restaurantId(1L)
                .customerId(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(20))
                .items(items)
                .build();
    }
} 