package com.quisin.analytics.repository;

import com.quisin.analytics.model.AnalyticsPeriod;
import com.quisin.analytics.model.SalesAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesAnalyticsRepository extends JpaRepository<SalesAnalytics, Long> {
    List<SalesAnalytics> findByRestaurantIdAndPeriodAndStartDateGreaterThanEqual(
        String restaurantId,
        AnalyticsPeriod period,
        LocalDateTime startDate
    );

    SalesAnalytics findFirstByRestaurantIdAndPeriodOrderByEndDateDesc(
        String restaurantId,
        AnalyticsPeriod period
    );

    void deleteByStartDateBefore(LocalDateTime date);
} 