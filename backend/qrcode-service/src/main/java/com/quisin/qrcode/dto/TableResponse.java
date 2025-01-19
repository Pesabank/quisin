package com.quisin.qrcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
    private Long id;
    private String restaurantId;
    private String tableNumber;
    private Integer capacity;
    private String description;
    private String qrCodeUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 