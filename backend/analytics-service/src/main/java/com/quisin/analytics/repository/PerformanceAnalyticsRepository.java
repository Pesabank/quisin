package com.quisin.analytics.repository;

import com.quisin.analytics.model.AnalyticsPeriod;
import com.quisin.analytics.model.PerformanceAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceAnalyticsRepository extends JpaRepository<PerformanceAnalytics, Long> {
    List<PerformanceAnalytics> findByRestaurantIdAndPeriodAndStartDateGreaterThanEqual(
        String restaurantId,
        AnalyticsPeriod period,
        LocalDateTime startDate
    );

    PerformanceAnalytics findFirstByRestaurantIdAndPeriodOrderByEndDateDesc(
        String restaurantId,
        AnalyticsPeriod period
    );

    void deleteByStartDateBefore(LocalDateTime date);
} 