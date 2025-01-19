package com.quisin.analytics.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

enum class AnalyticsCategory {
    SALES,
    ORDERS,
    RESERVATIONS,
    MENU_PERFORMANCE,
    CUSTOMER_BEHAVIOR
}

@Entity
@Table(name = "restaurant_analytics")
@EntityListeners(AuditingEntityListener::class)
data class RestaurantAnalytics(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val restaurantId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: AnalyticsCategory,

    @Column(nullable = false, precision = 10, scale = 2)
    val totalRevenue: BigDecimal,

    @Column(nullable = false)
    val totalOrders: Int,

    @Column(nullable = false)
    val uniqueCustomers: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val averageOrderValue: BigDecimal,

    @Column
    val topSellingItem: String? = null,

    @Column
    val peakHour: LocalDateTime? = null,

    @ElementCollection
    @CollectionTable(name = "revenue_breakdown", joinColumns = [JoinColumn(name = "analytics_id")])
    @Column(name = "category_revenue")
    val revenueBreakdown: Map<String, BigDecimal> = emptyMap(),

    @ElementCollection
    @CollectionTable(name = "order_distribution", joinColumns = [JoinColumn(name = "analytics_id")])
    @Column(name = "order_count")
    val orderDistribution: Map<String, Int> = emptyMap(),

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null
)

@Entity
@Table(name = "customer_analytics")
@EntityListeners(AuditingEntityListener::class)
data class CustomerAnalytics(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val customerId: UUID,

    @Column(nullable = false)
    val restaurantId: UUID,

    @Column(nullable = false)
    val totalVisits: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val totalSpend: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val averageSpendPerVisit: BigDecimal,

    @ElementCollection
    @CollectionTable(name = "favorite_items", joinColumns = [JoinColumn(name = "customer_analytics_id")])
    @Column(name = "menu_item")
    val favoriteItems: Map<String, Int> = emptyMap(),

    @Column
    val lastVisitDate: LocalDateTime? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null
)
