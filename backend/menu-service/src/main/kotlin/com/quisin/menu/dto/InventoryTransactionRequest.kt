package com.quisin.menu.dto

import com.quisin.menu.domain.InventoryTransactionType
import java.util.UUID

data class InventoryTransactionRequest(
    val restaurantId: UUID,
    val menuItemId: UUID,
    val type: InventoryTransactionType,
    val quantity: Int,
    val notes: String? = null,
    val employeeId: UUID
) 