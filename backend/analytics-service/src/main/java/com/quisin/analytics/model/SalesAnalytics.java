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
@Table(name = "sales_analytics")
@EqualsAndHashCode(callSuper = true)
public class SalesAnalytics extends BaseAnalytics {
    @Column(nullable = false)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private Integer totalOrders;

    @Column(nullable = false)
    private BigDecimal averageOrderValue;

    @Column(nullable = false)
    private Integer uniqueCustomers;

    @ElementCollection
    @CollectionTable(name = "sales_by_payment_method")
    @MapKeyColumn(name = "payment_method")
    @Column(name = "amount")
    private Map<String, BigDecimal> salesByPaymentMethod;

    @ElementCollection
    @CollectionTable(name = "sales_by_category")
    @MapKeyColumn(name = "category")
    @Column(name = "amount")
    private Map<String, BigDecimal> salesByCategory;

    @ElementCollection
    @CollectionTable(name = "top_selling_items")
    @MapKeyColumn(name = "item_id")
    @Column(name = "quantity")
    private Map<String, Integer> topSellingItems;

    @ElementCollection
    @CollectionTable(name = "hourly_sales_distribution")
    @MapKeyColumn(name = "hour")
    @Column(name = "amount")
    private Map<Integer, BigDecimal> hourlySalesDistribution;
} 