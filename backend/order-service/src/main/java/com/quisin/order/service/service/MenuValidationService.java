package com.quisin.order.service.service;

import com.quisin.order.service.client.MenuItemResponse;
import com.quisin.order.service.dto.OrderItemRequest;
import java.util.List;

public interface MenuValidationService {
    void validateMenuItems(Long restaurantId, List<OrderItemRequest> items);
    void validateMenuItemAvailability(Long restaurantId, OrderItemRequest item);
    void validateRestaurantMenuItems(Long restaurantId, List<OrderItemRequest> items);
    MenuItemResponse getMenuItem(Long restaurantId, Long menuItemId);
} 