package com.quisin.order.repository

import com.quisin.order.domain.Order
import com.quisin.order.domain.OrderStatus
import com.quisin.order.domain.OrderType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface OrderRepository : JpaRepository<Order, UUID> {
    fun findByRestaurantId(restaurantId: UUID): List<Order>
    fun findByCustomerId(customerId: UUID): List<Order>
    fun findByWaiterId(waiterId: UUID): List<Order>
    fun findByStatus(status: OrderStatus): List<Order>
    fun findByType(type: OrderType): List<Order>

    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.status = :status")
    fun findByRestaurantIdAndStatus(restaurantId: UUID, status: OrderStatus): List<Order>

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    fun findOrdersBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): List<Order>

    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate")
    fun findOrdersByRestaurantAndDateRange(
        restaurantId: UUID, 
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): List<Order>

    // Group Order specific methods
    fun findByGroupOrderId(groupOrderId: UUID): List<Order>
    fun findByGroupOrderIdAndIsGroupOrderLeaderTrue(groupOrderId: UUID): Order?

    @Query("SELECT o FROM Order o WHERE o.groupOrderId = :groupOrderId AND o.customerId = :customerId")
    fun findByGroupOrderIdAndCustomerId(groupOrderId: UUID, customerId: UUID): Order?

    @Query("SELECT o FROM Order o WHERE o.type = :type AND o.groupOrderId IS NOT NULL")
    fun findGroupOrders(type: OrderType): List<Order>
}
