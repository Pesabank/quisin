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
@Table(name = "customer_analytics")
@EqualsAndHashCode(callSuper = true)
public class CustomerAnalytics extends BaseAnalytics {
    @Column(nullable = false)
    private Integer newCustomers;

    @Column(nullable = false)
    private Integer returningCustomers;

    @Column(nullable = false)
    private BigDecimal averageCustomerLifetimeValue;

    @Column(nullable = false)
    private Double customerRetentionRate;

    @Column(nullable = false)
    private Double customerChurnRate;

    @ElementCollection
    @CollectionTable(name = "customer_segments")
    @MapKeyColumn(name = "segment")
    @Column(name = "count")
    private Map<String, Integer> customerSegments;

    @ElementCollection
    @CollectionTable(name = "customer_preferences")
    @MapKeyColumn(name = "category")
    @Column(name = "count")
    private Map<String, Integer> customerPreferences;

    @ElementCollection
    @CollectionTable(name = "customer_frequency")
    @MapKeyColumn(name = "visits")
    @Column(name = "count")
    private Map<Integer, Integer> customerFrequency;

    @Column(nullable = false)
    private Double averageVisitsPerMonth;

    @Column(nullable = false)
    private BigDecimal averageSpendPerVisit;
} 