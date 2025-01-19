package com.quisin.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "performance_analytics")
@EqualsAndHashCode(callSuper = true)
public class PerformanceAnalytics extends BaseAnalytics {
    @Column(nullable = false)
    private Double averagePreparationTime;

    @Column(nullable = false)
    private Double averageDeliveryTime;

    @Column(nullable = false)
    private Double orderCompletionRate;

    @Column(nullable = false)
    private Double customerSatisfactionScore;

    @Column(nullable = false)
    private Integer totalReviews;

    @ElementCollection
    @CollectionTable(name = "rating_distribution")
    @MapKeyColumn(name = "rating")
    @Column(name = "count")
    private Map<Integer, Integer> ratingDistribution;

    @ElementCollection
    @CollectionTable(name = "order_status_distribution")
    @MapKeyColumn(name = "status")
    @Column(name = "count")
    private Map<String, Integer> orderStatusDistribution;

    @ElementCollection
    @CollectionTable(name = "peak_hours")
    @MapKeyColumn(name = "hour")
    @Column(name = "order_count")
    private Map<Integer, Integer> peakHours;

    @Column(nullable = false)
    private Double tableUtilizationRate;

    @Column(nullable = false)
    private Integer cancelledReservations;
} 