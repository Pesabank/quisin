package com.quisin.reservation.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class TableStatus {
    AVAILABLE,   // Table is free and can be reserved
    RESERVED,    // Table is currently reserved
    OCCUPIED,    // Table is currently in use
    MAINTENANCE  // Table is temporarily unavailable
}

enum class TableType {
    SINGLE,      // Small table for 1-2 people
    COUPLE,      // Table for 2-3 people
    SMALL_GROUP, // Table for 4-6 people
    LARGE_GROUP, // Table for 7-10 people
    PRIVATE      // Private dining area or special section
}

@Entity
@Table(name = "restaurant_tables")
@EntityListeners(AuditingEntityListener::class)
data class RestaurantTable(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val restaurantId: UUID,

    @Column(nullable = false)
    val tableNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TableStatus = TableStatus.AVAILABLE,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: TableType,

    @Column(nullable = false)
    val capacity: Int,

    @Column
    val location: String? = null,  // e.g., "Indoor", "Outdoor", "Terrace"

    @Column
    val description: String? = null,

    @Column
    val minimumSpend: Double? = null,  // Optional minimum spend for certain tables

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    val updatedAt: LocalDateTime? = null
)
