package com.quisin.reservation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "restaurant-service", path = "/api/v1/restaurants")
public interface RestaurantServiceClient {

    @GetMapping("/{restaurantId}/tables")
    List<TableDto> getRestaurantTables(@PathVariable String restaurantId);

    @GetMapping("/{restaurantId}/tables/{tableId}")
    TableDto getTable(
        @PathVariable String restaurantId,
        @PathVariable String tableId
    );

    @GetMapping("/{restaurantId}/tables/available")
    List<TableDto> getAvailableTables(
        @PathVariable String restaurantId,
        @RequestParam int capacity
    );

    @GetMapping("/{restaurantId}/operating-hours/check")
    boolean isRestaurantOperating(
        @PathVariable String restaurantId,
        @RequestParam String dateTime
    );
} 