package com.quisin.order.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

enum class OrderStatus {
    PENDING, 
    CONFIRMED, 
    PREPARING, 
    READY_FOR_PICKUP, 
    COMPLETED, 
    CANCELLED
}

enum class OrderType {
    SINGLE, 
    GROUP
}

enum class WaiterAssignmentStatus {
    PENDING,      // Waiter assignment requested but not yet assigned
    ASSIGNED,     // Waiter has been assigned to the order
    DECLINED,     // Waiter declined the order assignment
    COMPLETED     // Order served by the assigned waiter
}

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener::class)
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val restaurantId: UUID,

    @Column(nullable = false)
    val customerId: UUID,

    @Column
    val assignedWaiterId: UUID? = null,

    @Column
    val waiterId: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: OrderType,

    @Column(nullable = false, precision = 10, scale = 2)
    val totalAmount: BigDecimal,

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = [JoinColumn(name = "order_id")])
    val items: List<OrderItem> = listOf(),

    @Column
    val tableNumber: String? = null,

    @Column
    val specialInstructions: String? = null,

    @Column
    val groupOrderId: UUID? = null,

    @Column(nullable = false)
    val isGroupOrderLeader: Boolean = false,

    @Column(nullable = false)
    val isWaiterAssignmentRequested: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column
    val waiterAssignmentStatus: WaiterAssignmentStatus? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    val updatedAt: LocalDateTime? = null
)

@Embeddable
data class OrderItem(
    @Column(nullable = false)
    val menuItemId: UUID,

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column
    val specialInstructions: String? = null
)
