package com.quisin.order.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MenuServiceFallback implements MenuServiceClient {
    @Override
    public MenuItemResponse getMenuItem(Long restaurantId, Long menuItemId) {
        log.error("Fallback: Unable to get menu item with ID {} for restaurant {}", menuItemId, restaurantId);
        return MenuItemResponse.builder()
                .id(menuItemId)
                .restaurantId(restaurantId)
                .name("Unavailable Item")
                .description("This item is currently unavailable")
                .price(null)
                .available(false)
                .build();
    }
} 