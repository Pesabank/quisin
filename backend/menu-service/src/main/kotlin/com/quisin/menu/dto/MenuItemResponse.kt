package com.quisin.menu.dto

import com.quisin.menu.domain.MenuItemCategory
import com.quisin.menu.domain.MenuItemStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class MenuItemResponse(
    val id: UUID,
    val restaurantId: UUID,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val category: MenuItemCategory,
    val imageUrl: String?,
    val currentStock: Int,
    val maxStock: Int,
    val status: MenuItemStatus,
    val preparationTime: Int?,
    val calories: Int?,
    val allergens: List<String>,
    val tags: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 