package com.quisin.order.service.service.impl;

import com.quisin.order.service.dto.*;
import com.quisin.order.service.event.OrderEventPublisher;
import com.quisin.order.service.model.*;
import com.quisin.order.service.repository.OrderItemRepository;
import com.quisin.order.service.repository.OrderRepository;
import com.quisin.order.service.service.MenuValidationService;
import com.quisin.order.service.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuValidationService menuValidationService;
    private final OrderEventPublisher eventPublisher;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        menuValidationService.validateMenuItems(request.getRestaurantId(), request.getItems());

        Order order = Order.builder()
                .restaurantId(request.getRestaurantId())
                .customerId(request.getCustomerId())
                .tableId(request.getTableId())
                .type(request.getType())
                .status(OrderStatus.PENDING)
                .specialInstructions(request.getSpecialInstructions())
                .groupOrderId(request.getGroupOrderId())
                .isWaiterAssignmentRequested(request.getIsWaiterAssignmentRequested())
                .waiterAssignmentStatus(request.getIsWaiterAssignmentRequested() ? WaiterAssignmentStatus.PENDING : null)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            var menuItem = menuValidationService.getMenuItem(request.getRestaurantId(), itemRequest.getMenuItemId());
            OrderItem item = OrderItem.builder()
                    .menuItemId(itemRequest.getMenuItemId())
                    .menuItemName(menuItem.getName())
                    .quantity(itemRequest.getQuantity())
                    .specialInstructions(itemRequest.getSpecialInstructions())
                    .status(OrderItemStatus.PENDING)
                    .unitPrice(menuItem.getPrice())
                    .build();
            order.addOrderItem(item);
            item.calculateSubtotal();
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publishOrderEvent(savedOrder);
        return mapOrderToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        return mapOrderToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        
        order.setStatus(request.getStatus());
        if (request.getStatus() == OrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
        }
        
        Order updatedOrder = orderRepository.save(order);
        eventPublisher.publishOrderEvent(updatedOrder);
        return mapOrderToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse assignWaiter(Long orderId, AssignWaiterRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        
        if (!order.getIsWaiterAssignmentRequested()) {
            throw new IllegalStateException("Waiter assignment was not requested for this order");
        }
        
        order.setWaiterId(request.getWaiterId());
        order.setWaiterAssignmentStatus(WaiterAssignmentStatus.ASSIGNED);
        
        Order updatedOrder = orderRepository.save(order);
        eventPublisher.publishOrderEvent(updatedOrder);
        return mapOrderToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        eventPublisher.publishOrderEvent(cancelledOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getCustomerOrders(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getRestaurantOrders(Long restaurantId, Pageable pageable) {
        return orderRepository.findByRestaurantId(restaurantId, pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getWaiterOrders(Long waiterId, Pageable pageable) {
        return orderRepository.findByWaiterId(waiterId, pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getGroupOrders(Long groupOrderId) {
        return orderRepository.findByGroupOrderId(groupOrderId)
                .stream()
                .map(this::mapOrderToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getRestaurantOrdersByStatus(Long restaurantId, List<String> statuses, Pageable pageable) {
        List<OrderStatus> orderStatuses = statuses.stream()
                .map(OrderStatus::valueOf)
                .collect(Collectors.toList());
        
        return orderRepository.findByRestaurantIdAndStatusIn(restaurantId, orderStatuses, pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getWaiterOrdersByStatus(Long waiterId, List<String> statuses, Pageable pageable) {
        List<OrderStatus> orderStatuses = statuses.stream()
                .map(OrderStatus::valueOf)
                .collect(Collectors.toList());
        
        return orderRepository.findByWaiterIdAndStatusIn(waiterId, orderStatuses, pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getRestaurantOrdersByDateRange(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findByRestaurantIdAndDateRange(restaurantId, startDate, endDate, pageable)
                .map(this::mapOrderToResponse);
    }

    private OrderResponse mapOrderToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .restaurantId(order.getRestaurantId())
                .customerId(order.getCustomerId())
                .waiterId(order.getWaiterId())
                .tableId(order.getTableId())
                .type(order.getType())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems().stream()
                        .map(this::mapOrderItemToResponse)
                        .collect(Collectors.toList()))
                .specialInstructions(order.getSpecialInstructions())
                .groupOrderId(order.getGroupOrderId())
                .isWaiterAssignmentRequested(order.getIsWaiterAssignmentRequested())
                .waiterAssignmentStatus(order.getWaiterAssignmentStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .completedAt(order.getCompletedAt())
                .build();
    }

    private OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItemId())
                .menuItemName(item.getMenuItemName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .specialInstructions(item.getSpecialInstructions())
                .status(item.getStatus())
                .build();
    }
} 