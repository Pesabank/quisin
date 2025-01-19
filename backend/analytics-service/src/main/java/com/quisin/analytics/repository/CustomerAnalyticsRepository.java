package com.quisin.analytics.repository;

import com.quisin.analytics.model.AnalyticsPeriod;
import com.quisin.analytics.model.CustomerAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerAnalyticsRepository extends JpaRepository<CustomerAnalytics, Long> {
    List<CustomerAnalytics> findByRestaurantIdAndPeriodAndStartDateGreaterThanEqual(
        String restaurantId,
        AnalyticsPeriod period,
        LocalDateTime startDate
    );

    CustomerAnalytics findFirstByRestaurantIdAndPeriodOrderByEndDateDesc(
        String restaurantId,
        AnalyticsPeriod period
    );

    void deleteByStartDateBefore(LocalDateTime date);
} 