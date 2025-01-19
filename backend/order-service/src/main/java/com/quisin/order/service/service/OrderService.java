package com.quisin.order.service.service;

import com.quisin.order.service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    
    OrderResponse getOrderById(Long orderId);
    
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
    
    OrderResponse assignWaiter(Long orderId, AssignWaiterRequest request);
    
    void cancelOrder(Long orderId);
    
    Page<OrderResponse> getCustomerOrders(Long customerId, Pageable pageable);
    
    Page<OrderResponse> getRestaurantOrders(Long restaurantId, Pageable pageable);
    
    Page<OrderResponse> getWaiterOrders(Long waiterId, Pageable pageable);
    
    List<OrderResponse> getGroupOrders(Long groupOrderId);
    
    Page<OrderResponse> getRestaurantOrdersByStatus(Long restaurantId, List<String> statuses, Pageable pageable);
    
    Page<OrderResponse> getWaiterOrdersByStatus(Long waiterId, List<String> statuses, Pageable pageable);
    
    Page<OrderResponse> getRestaurantOrdersByDateRange(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
} 