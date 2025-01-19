package com.quisin.order.service.service.impl;

import com.quisin.order.service.client.MenuItemResponse;
import com.quisin.order.service.dto.*;
import com.quisin.order.service.event.OrderEventPublisher;
import com.quisin.order.service.model.*;
import com.quisin.order.service.repository.OrderItemRepository;
import com.quisin.order.service.repository.OrderRepository;
import com.quisin.order.service.service.MenuValidationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private MenuValidationService menuValidationService;

    @Mock
    private OrderEventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private static final Long ORDER_ID = 1L;
    private static final Long RESTAURANT_ID = 1L;
    private static final Long CUSTOMER_ID = 1L;
    private static final Long WAITER_ID = 1L;
    private static final Long MENU_ITEM_ID = 1L;
    private static final Long ORDER_ITEM_ID = 1L;

    @Test
    void createOrder_Success() {
        // Arrange
        CreateOrderRequest request = createOrderRequest();
        MenuItemResponse menuItem = MenuItemResponse.builder()
                .id(MENU_ITEM_ID)
                .name("Test Item")
                .price(BigDecimal.valueOf(10))
                .build();
        when(menuValidationService.getMenuItem(any(), any())).thenReturn(menuItem);
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder)
                .satisfies(order -> {
                    assertThat(order.getRestaurantId()).isEqualTo(RESTAURANT_ID);
                    assertThat(order.getCustomerId()).isEqualTo(CUSTOMER_ID);
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
                    assertThat(order.getTotalAmount()).isEqualTo(BigDecimal.valueOf(20));
                });
        verify(eventPublisher).publishOrderEvent(any());
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        Order order = createOrder();
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        // Act
        OrderResponse response = orderService.getOrderById(ORDER_ID);

        // Assert
        assertThat(response)
                .satisfies(r -> {
                    assertThat(r.getId()).isEqualTo(ORDER_ID);
                    assertThat(r.getRestaurantId()).isEqualTo(RESTAURANT_ID);
                    assertThat(r.getCustomerId()).isEqualTo(CUSTOMER_ID);
                });
    }

    @Test
    void getOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.getOrderById(ORDER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void updateOrderStatus_Success() {
        // Arrange
        Order order = createOrder();
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        OrderResponse response = orderService.updateOrderStatus(ORDER_ID, request);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(eventPublisher).publishOrderEvent(any());
    }

    @Test
    void assignWaiter_Success() {
        // Arrange
        Order order = createOrder();
        order.setIsWaiterAssignmentRequested(true);
        AssignWaiterRequest request = new AssignWaiterRequest(WAITER_ID);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        OrderResponse response = orderService.assignWaiter(ORDER_ID, request);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder)
                .satisfies(o -> {
                    assertThat(o.getWaiterId()).isEqualTo(WAITER_ID);
                    assertThat(o.getWaiterAssignmentStatus()).isEqualTo(WaiterAssignmentStatus.ASSIGNED);
                });
        verify(eventPublisher).publishOrderEvent(any());
    }

    @Test
    void assignWaiter_NotRequested() {
        // Arrange
        Order order = createOrder();
        order.setIsWaiterAssignmentRequested(false);
        AssignWaiterRequest request = new AssignWaiterRequest(WAITER_ID);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.assignWaiter(ORDER_ID, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Waiter assignment was not requested");
    }

    @Test
    void cancelOrder_Success() {
        // Arrange
        Order order = createOrder();
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        orderService.cancelOrder(ORDER_ID);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(eventPublisher).publishOrderEvent(any());
    }

    @Test
    void getCustomerOrders_Success() {
        // Arrange
        List<Order> orders = List.of(createOrder(), createOrder());
        Page<Order> orderPage = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findByCustomerId(CUSTOMER_ID, pageable)).thenReturn(orderPage);

        // Act
        Page<OrderResponse> response = orderService.getCustomerOrders(CUSTOMER_ID, pageable);

        // Assert
        assertThat(response).hasSize(2);
        verify(orderRepository).findByCustomerId(CUSTOMER_ID, pageable);
    }

    private CreateOrderRequest createOrderRequest() {
        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .menuItemId(MENU_ITEM_ID)
                .quantity(2)
                .build();

        return CreateOrderRequest.builder()
                .restaurantId(RESTAURANT_ID)
                .customerId(CUSTOMER_ID)
                .type(OrderType.DINE_IN)
                .items(List.of(itemRequest))
                .isWaiterAssignmentRequested(true)
                .build();
    }

    private Order createOrder() {
        OrderItem item = OrderItem.builder()
                .id(ORDER_ITEM_ID)
                .menuItemId(MENU_ITEM_ID)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(10))
                .subtotal(BigDecimal.valueOf(20))
                .build();

        Order order = Order.builder()
                .id(ORDER_ID)
                .restaurantId(RESTAURANT_ID)
                .customerId(CUSTOMER_ID)
                .type(OrderType.DINE_IN)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(20))
                .isWaiterAssignmentRequested(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        order.getItems().add(item);
        return order;
    }
} 