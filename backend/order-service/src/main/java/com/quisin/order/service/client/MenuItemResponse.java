package com.quisin.order.service.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private Long id;
    private Long restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean available;
    private String category;
    private String imageUrl;
} 