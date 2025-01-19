package com.quisin.analytics.service;

import com.quisin.analytics.dto.AnalyticsRequest;
import com.quisin.analytics.dto.AnalyticsResponse;
import com.quisin.analytics.model.AnalyticsPeriod;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsService {
    AnalyticsResponse getAnalytics(AnalyticsRequest request);
    
    List<AnalyticsResponse> getAnalyticsHistory(
        String restaurantId,
        AnalyticsPeriod period,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
    
    void processOrderEvent(String event);
    void processPaymentEvent(String event);
    void processReservationEvent(String event);
    void processReviewEvent(String event);
    
    void generateAnalytics(String restaurantId);
    void cleanupOldData(int retentionDays);
} 