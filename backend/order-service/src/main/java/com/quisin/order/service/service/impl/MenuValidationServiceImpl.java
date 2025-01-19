package com.quisin.order.service.service.impl;

import com.quisin.order.service.client.MenuServiceClient;
import com.quisin.order.service.client.MenuItemResponse;
import com.quisin.order.service.dto.OrderItemRequest;
import com.quisin.order.service.exception.MenuItemNotFoundException;
import com.quisin.order.service.exception.MenuItemNotAvailableException;
import com.quisin.order.service.exception.RestaurantMenuItemMismatchException;
import com.quisin.order.service.service.MenuValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuValidationServiceImpl implements MenuValidationService {
    private final MenuServiceClient menuServiceClient;

    @Override
    public void validateMenuItems(Long restaurantId, List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            MenuItemResponse menuItem = menuServiceClient.getMenuItem(restaurantId, item.getMenuItemId());
            if (!menuItem.getAvailable()) {
                throw new MenuItemNotAvailableException("Menu item with ID " + item.getMenuItemId() + " is not available");
            }
            if (!menuItem.getRestaurantId().equals(restaurantId)) {
                throw new RestaurantMenuItemMismatchException("Menu item with ID " + item.getMenuItemId() + " does not belong to restaurant " + restaurantId);
            }
        }
    }

    @Override
    public void validateMenuItemAvailability(Long restaurantId, OrderItemRequest item) {
        MenuItemResponse menuItem = menuServiceClient.getMenuItem(restaurantId, item.getMenuItemId());
        if (!menuItem.getAvailable()) {
            throw new MenuItemNotAvailableException("Menu item with ID " + item.getMenuItemId() + " is not available");
        }
    }

    @Override
    public void validateRestaurantMenuItems(Long restaurantId, List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            MenuItemResponse menuItem = menuServiceClient.getMenuItem(restaurantId, item.getMenuItemId());
            if (!menuItem.getRestaurantId().equals(restaurantId)) {
                throw new RestaurantMenuItemMismatchException("Menu item with ID " + item.getMenuItemId() + " does not belong to restaurant " + restaurantId);
            }
        }
    }

    @Override
    public MenuItemResponse getMenuItem(Long restaurantId, Long menuItemId) {
        return menuServiceClient.getMenuItem(restaurantId, menuItemId);
    }
} 