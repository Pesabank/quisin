package com.quisin.reservation.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDto {
    private String id;
    private String number;
    private int capacity;
    private String location;
    private boolean accessible;
    private TableStatus status;
    private String restaurantId;

    public enum TableStatus {
        AVAILABLE,
        OCCUPIED,
        RESERVED,
        OUT_OF_SERVICE
    }
} 