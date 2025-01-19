package com.quisin.analytics.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quisin.analytics.dto.AnalyticsRequest;
import com.quisin.analytics.dto.AnalyticsResponse;
import com.quisin.analytics.model.*;
import com.quisin.analytics.repository.*;
import com.quisin.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private final SalesAnalyticsRepository salesAnalyticsRepository;
    private final PerformanceAnalyticsRepository performanceAnalyticsRepository;
    private final CustomerAnalyticsRepository customerAnalyticsRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "analyticsCache", key = "#request.toString()")
    public AnalyticsResponse getAnalytics(AnalyticsRequest request) {
        log.info("Fetching analytics for restaurant: {} with period: {}", 
                request.getRestaurantId(), request.getPeriod());

        AnalyticsResponse.AnalyticsResponseBuilder responseBuilder = AnalyticsResponse.builder()
                .restaurantId(request.getRestaurantId())
                .period(request.getPeriod())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate());

        if (request.isIncludeSales()) {
            SalesAnalytics salesAnalytics = salesAnalyticsRepository
                    .findFirstByRestaurantIdAndPeriodOrderByEndDateDesc(
                            request.getRestaurantId(), request.getPeriod());
            responseBuilder.sales(mapSalesAnalytics(salesAnalytics));
        }

        if (request.isIncludePerformance()) {
            PerformanceAnalytics performanceAnalytics = performanceAnalyticsRepository
                    .findFirstByRestaurantIdAndPeriodOrderByEndDateDesc(
                            request.getRestaurantId(), request.getPeriod());
            responseBuilder.performance(mapPerformanceAnalytics(performanceAnalytics));
        }

        if (request.isIncludeCustomer()) {
            CustomerAnalytics customerAnalytics = customerAnalyticsRepository
                    .findFirstByRestaurantIdAndPeriodOrderByEndDateDesc(
                            request.getRestaurantId(), request.getPeriod());
            responseBuilder.customer(mapCustomerAnalytics(customerAnalytics));
        }

        return responseBuilder.build();
    }

    @Override
    public List<AnalyticsResponse> getAnalyticsHistory(
            String restaurantId,
            AnalyticsPeriod period,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        log.info("Fetching analytics history for restaurant: {} from {} to {}", 
                restaurantId, startDate, endDate);

        List<SalesAnalytics> salesHistory = salesAnalyticsRepository
                .findByRestaurantIdAndPeriodAndStartDateGreaterThanEqual(
                        restaurantId, period, startDate);

        return salesHistory.stream()
                .map(sales -> {
                    PerformanceAnalytics performance = performanceAnalyticsRepository
                            .findFirstByRestaurantIdAndPeriodOrderByEndDateDesc(
                                    restaurantId, period);
                    CustomerAnalytics customer = customerAnalyticsRepository
                            .findFirstByRestaurantIdAndPeriodOrderByEndDateDesc(
                                    restaurantId, period);

                    return AnalyticsResponse.builder()
                            .restaurantId(restaurantId)
                            .period(period)
                            .startDate(sales.getStartDate())
                            .endDate(sales.getEndDate())
                            .sales(mapSalesAnalytics(sales))
                            .performance(mapPerformanceAnalytics(performance))
                            .customer(mapCustomerAnalytics(customer))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processOrderEvent(String event) {
        try {
            // Process order event and update analytics
            log.info("Processing order event: {}", event);
            // Implementation details for order event processing
        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void processPaymentEvent(String event) {
        try {
            // Process payment event and update analytics
            log.info("Processing payment event: {}", event);
            // Implementation details for payment event processing
        } catch (Exception e) {
            log.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void processReservationEvent(String event) {
        try {
            // Process reservation event and update analytics
            log.info("Processing reservation event: {}", event);
            // Implementation details for reservation event processing
        } catch (Exception e) {
            log.error("Error processing reservation event: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void processReviewEvent(String event) {
        try {
            // Process review event and update analytics
            log.info("Processing review event: {}", event);
            // Implementation details for review event processing
        } catch (Exception e) {
            log.error("Error processing review event: {}", e.getMessage(), e);
        }
    }

    @Override
    @Scheduled(fixedRateString = "${analytics.metrics.update-interval}")
    @Transactional
    public void generateAnalytics(String restaurantId) {
        log.info("Generating analytics for restaurant: {}", restaurantId);
        // Implementation for periodic analytics generation
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    @Transactional
    public void cleanupOldData(int retentionDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
        log.info("Cleaning up analytics data older than: {}", cutoffDate);
        
        salesAnalyticsRepository.deleteByStartDateBefore(cutoffDate);
        performanceAnalyticsRepository.deleteByStartDateBefore(cutoffDate);
        customerAnalyticsRepository.deleteByStartDateBefore(cutoffDate);
    }

    private SalesAnalyticsDTO mapSalesAnalytics(SalesAnalytics analytics) {
        if (analytics == null) return null;
        return SalesAnalyticsDTO.builder()
                .totalRevenue(analytics.getTotalRevenue().doubleValue())
                .totalOrders(analytics.getTotalOrders())
                .averageOrderValue(analytics.getAverageOrderValue().doubleValue())
                .uniqueCustomers(analytics.getUniqueCustomers())
                .salesByPaymentMethod(analytics.getSalesByPaymentMethod().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().doubleValue())))
                .salesByCategory(analytics.getSalesByCategory().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().doubleValue())))
                .topSellingItems(analytics.getTopSellingItems())
                .hourlySalesDistribution(analytics.getHourlySalesDistribution().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().doubleValue())))
                .build();
    }

    private PerformanceAnalyticsDTO mapPerformanceAnalytics(PerformanceAnalytics analytics) {
        if (analytics == null) return null;
        return PerformanceAnalyticsDTO.builder()
                .averagePreparationTime(analytics.getAveragePreparationTime())
                .averageDeliveryTime(analytics.getAverageDeliveryTime())
                .orderCompletionRate(analytics.getOrderCompletionRate())
                .customerSatisfactionScore(analytics.getCustomerSatisfactionScore())
                .totalReviews(analytics.getTotalReviews())
                .ratingDistribution(analytics.getRatingDistribution())
                .orderStatusDistribution(analytics.getOrderStatusDistribution())
                .peakHours(analytics.getPeakHours())
                .tableUtilizationRate(analytics.getTableUtilizationRate())
                .cancelledReservations(analytics.getCancelledReservations())
                .build();
    }

    private CustomerAnalyticsDTO mapCustomerAnalytics(CustomerAnalytics analytics) {
        if (analytics == null) return null;
        return CustomerAnalyticsDTO.builder()
                .newCustomers(analytics.getNewCustomers())
                .returningCustomers(analytics.getReturningCustomers())
                .averageCustomerLifetimeValue(analytics.getAverageCustomerLifetimeValue().doubleValue())
                .customerRetentionRate(analytics.getCustomerRetentionRate())
                .customerChurnRate(analytics.getCustomerChurnRate())
                .customerSegments(analytics.getCustomerSegments())
                .customerPreferences(analytics.getCustomerPreferences())
                .customerFrequency(analytics.getCustomerFrequency())
                .averageVisitsPerMonth(analytics.getAverageVisitsPerMonth())
                .averageSpendPerVisit(analytics.getAverageSpendPerVisit().doubleValue())
                .build();
    }
} 