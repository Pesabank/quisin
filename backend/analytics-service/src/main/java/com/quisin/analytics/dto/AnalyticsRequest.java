package com.quisin.analytics.dto;

import com.quisin.analytics.model.AnalyticsPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRequest {
    private String restaurantId;
    private AnalyticsPeriod period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean includeSales;
    private boolean includePerformance;
    private boolean includeCustomer;
} 