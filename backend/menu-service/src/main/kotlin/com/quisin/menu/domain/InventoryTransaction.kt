package com.quisin.menu.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "inventory_transactions")
data class InventoryTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false)
    val restaurantId: UUID,

    @Column(nullable = false)
    val menuItemId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: InventoryTransactionType,

    @Column(nullable = false)
    val quantity: Int,

    @Column
    val notes: String? = null,

    @Column(nullable = false)
    val employeeId: UUID,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class InventoryTransactionType {
    STOCK_IN,
    STOCK_OUT,
    SPOILAGE,
    ADJUSTMENT
} 