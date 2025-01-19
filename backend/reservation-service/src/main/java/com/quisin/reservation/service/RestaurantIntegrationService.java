package com.quisin.reservation.service;

import com.quisin.reservation.client.RestaurantServiceClient;
import com.quisin.reservation.client.TableDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantIntegrationService {

    private final RestaurantServiceClient restaurantServiceClient;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @CircuitBreaker(name = "restaurantService", fallbackMethod = "getRestaurantTablesFallback")
    @Retry(name = "restaurantService")
    public List<TableDto> getRestaurantTables(String restaurantId) {
        return restaurantServiceClient.getRestaurantTables(restaurantId);
    }

    @CircuitBreaker(name = "restaurantService", fallbackMethod = "getTableFallback")
    @Retry(name = "restaurantService")
    public TableDto getTable(String restaurantId, String tableId) {
        return restaurantServiceClient.getTable(restaurantId, tableId);
    }

    @CircuitBreaker(name = "restaurantService", fallbackMethod = "getAvailableTablesFallback")
    @Retry(name = "restaurantService")
    public List<TableDto> getAvailableTables(String restaurantId, int capacity) {
        return restaurantServiceClient.getAvailableTables(restaurantId, capacity);
    }

    @CircuitBreaker(name = "restaurantService", fallbackMethod = "isRestaurantOperatingFallback")
    @Retry(name = "restaurantService")
    public boolean isRestaurantOperating(String restaurantId, LocalDateTime dateTime) {
        return restaurantServiceClient.isRestaurantOperating(
            restaurantId,
            dateTime.format(DATE_TIME_FORMATTER)
        );
    }

    // Fallback methods
    private List<TableDto> getRestaurantTablesFallback(String restaurantId, Exception ex) {
        log.error("Failed to get restaurant tables for restaurant {}: {}", restaurantId, ex.getMessage());
        return Collections.emptyList();
    }

    private TableDto getTableFallback(String restaurantId, String tableId, Exception ex) {
        log.error("Failed to get table {} for restaurant {}: {}", tableId, restaurantId, ex.getMessage());
        return null;
    }

    private List<TableDto> getAvailableTablesFallback(String restaurantId, int capacity, Exception ex) {
        log.error("Failed to get available tables for restaurant {} with capacity {}: {}", 
            restaurantId, capacity, ex.getMessage());
        return Collections.emptyList();
    }

    private boolean isRestaurantOperatingFallback(String restaurantId, LocalDateTime dateTime, Exception ex) {
        log.error("Failed to check operating hours for restaurant {}: {}", restaurantId, ex.getMessage());
        return false;
    }
} 