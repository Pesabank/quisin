package com.quisin.order.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "menu-service", fallback = MenuServiceFallback.class)
public interface MenuServiceClient {
    @GetMapping("/api/v1/restaurants/{restaurantId}/menu-items/{menuItemId}")
    MenuItemResponse getMenuItem(@PathVariable("restaurantId") Long restaurantId, @PathVariable("menuItemId") Long menuItemId);
} 