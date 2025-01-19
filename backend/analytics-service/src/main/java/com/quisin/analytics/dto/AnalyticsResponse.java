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
public class AnalyticsResponse {
    private String restaurantId;
    private AnalyticsPeriod period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SalesAnalyticsDTO sales;
    private PerformanceAnalyticsDTO performance;
    private CustomerAnalyticsDTO customer;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SalesAnalyticsDTO {
    private Double totalRevenue;
    private Integer totalOrders;
    private Double averageOrderValue;
    private Integer uniqueCustomers;
    private java.util.Map<String, Double> salesByPaymentMethod;
    private java.util.Map<String, Double> salesByCategory;
    private java.util.Map<String, Integer> topSellingItems;
    private java.util.Map<Integer, Double> hourlySalesDistribution;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PerformanceAnalyticsDTO {
    private Double averagePreparationTime;
    private Double averageDeliveryTime;
    private Double orderCompletionRate;
    private Double customerSatisfactionScore;
    private Integer totalReviews;
    private java.util.Map<Integer, Integer> ratingDistribution;
    private java.util.Map<String, Integer> orderStatusDistribution;
    private java.util.Map<Integer, Integer> peakHours;
    private Double tableUtilizationRate;
    private Integer cancelledReservations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CustomerAnalyticsDTO {
    private Integer newCustomers;
    private Integer returningCustomers;
    private Double averageCustomerLifetimeValue;
    private Double customerRetentionRate;
    private Double customerChurnRate;
    private java.util.Map<String, Integer> customerSegments;
    private java.util.Map<String, Integer> customerPreferences;
    private java.util.Map<Integer, Integer> customerFrequency;
    private Double averageVisitsPerMonth;
    private Double averageSpendPerVisit;
} 