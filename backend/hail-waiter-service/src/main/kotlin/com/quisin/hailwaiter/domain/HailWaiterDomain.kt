package com.quisin.hailwaiter.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class WaiterRequestStatus {
    PENDING,
    IN_PROGRESS,
    RESOLVED,
    CANCELLED
}

enum class WaiterRequestReason {
    MENU_ASSISTANCE,
    ORDER_PLACEMENT,
    ADDITIONAL_SERVICE,
    BILL_REQUEST,
    GENERAL_INQUIRY
}

@Entity
@Table(name = "waiter_requests")
@EntityListeners(AuditingEntityListener::class)
data class WaiterRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val restaurantId: UUID,

    @Column(nullable = false)
    val tableId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val reason: WaiterRequestReason,

    @Enumerated(EnumType.STRING)
    var status: WaiterRequestStatus = WaiterRequestStatus.PENDING,

    @Column(length = 500)
    val additionalDetails: String? = null,

    @Column(nullable = false)
    val assignedWaiterId: UUID? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
