package com.quisin.reservation.mock;

import com.quisin.reservation.client.RestaurantServiceClient;
import com.quisin.reservation.client.TableDto;
import org.springframework.boot.test.context.TestComponent;

import java.util.Arrays;
import java.util.List;

@TestComponent
public class RestaurantServiceMock implements RestaurantServiceClient {

    private static final String TEST_RESTAURANT_ID = "test-restaurant";
    private static final String VALID_TABLE_ID = "table-1";
    private static final String INVALID_TABLE_ID = "invalid-table";

    @Override
    public List<TableDto> getRestaurantTables(String restaurantId) {
        if (TEST_RESTAURANT_ID.equals(restaurantId)) {
            return Arrays.asList(
                createTable(VALID_TABLE_ID, "1", 4, TableDto.TableStatus.AVAILABLE),
                createTable("table-2", "2", 6, TableDto.TableStatus.AVAILABLE),
                createTable("table-3", "3", 2, TableDto.TableStatus.OCCUPIED)
            );
        }
        return List.of();
    }

    @Override
    public TableDto getTable(String restaurantId, String tableId) {
        if (TEST_RESTAURANT_ID.equals(restaurantId) && VALID_TABLE_ID.equals(tableId)) {
            return createTable(VALID_TABLE_ID, "1", 4, TableDto.TableStatus.AVAILABLE);
        }
        if (INVALID_TABLE_ID.equals(tableId)) {
            return createTable(INVALID_TABLE_ID, "X", 4, TableDto.TableStatus.OUT_OF_SERVICE);
        }
        return null;
    }

    @Override
    public List<TableDto> getAvailableTables(String restaurantId, int capacity) {
        if (TEST_RESTAURANT_ID.equals(restaurantId)) {
            return Arrays.asList(
                createTable(VALID_TABLE_ID, "1", 4, TableDto.TableStatus.AVAILABLE),
                createTable("table-2", "2", 6, TableDto.TableStatus.AVAILABLE)
            ).stream()
                .filter(table -> table.getCapacity() >= capacity)
                .toList();
        }
        return List.of();
    }

    @Override
    public boolean isRestaurantOperating(String restaurantId, String dateTime) {
        return TEST_RESTAURANT_ID.equals(restaurantId);
    }

    private TableDto createTable(String id, String number, int capacity, TableDto.TableStatus status) {
        return new TableDto(
            id,
            number,
            capacity,
            "Main Floor",
            true,
            status,
            TEST_RESTAURANT_ID
        );
    }

    public static String getTestRestaurantId() {
        return TEST_RESTAURANT_ID;
    }

    public static String getValidTableId() {
        return VALID_TABLE_ID;
    }

    public static String getInvalidTableId() {
        return INVALID_TABLE_ID;
    }
} 