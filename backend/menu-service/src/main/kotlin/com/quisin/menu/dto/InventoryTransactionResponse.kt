package com.quisin.menu.dto

import com.quisin.menu.domain.InventoryTransactionType
import java.time.LocalDateTime
import java.util.UUID

data class InventoryTransactionResponse(
    val id: UUID,
    val restaurantId: UUID,
    val menuItemId: UUID,
    val type: InventoryTransactionType,
    val quantity: Int,
    val notes: String?,
    val employeeId: UUID,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 