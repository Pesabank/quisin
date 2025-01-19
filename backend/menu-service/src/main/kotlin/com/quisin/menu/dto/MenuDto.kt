package com.quisin.menu.dto

import com.quisin.menu.domain.*
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class MenuDto(
    val id: String?,
    val name: String,
    val description: String,
    val restaurantId: String,
    val categories: List<MenuCategoryDto>,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class MenuCategoryDto(
    val id: String?,
    val name: String,
    val description: String,
    val items: List<MenuItemDto>,
    val displayOrder: Int,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class MenuItemDto(
    val id: UUID?,
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
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class CreateMenuRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    val description: String,

    @field:NotBlank(message = "Restaurant ID is required")
    val restaurantId: String,

    @field:Valid
    @field:NotEmpty(message = "At least one category is required")
    val categories: List<CreateMenuCategoryRequest>
)

data class CreateMenuCategoryRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    val description: String,

    @field:Valid
    @field:NotEmpty(message = "At least one item is required")
    val items: List<CreateMenuItemRequest>,

    @field:Min(0)
    val displayOrder: Int
)

data class CreateMenuItemRequest(
    @field:NotNull(message = "Restaurant ID is required")
    val restaurantId: UUID,

    @field:NotBlank(message = "Menu item name is required")
    @field:Size(min = 2, max = 100, message = "Menu item name must be between 2 and 100 characters")
    val name: String,

    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,

    @field:Positive(message = "Price must be a positive number")
    val price: BigDecimal,

    @field:NotNull(message = "Menu item category is required")
    val category: MenuItemCategory,

    val imageUrl: String? = null,

    @field:PositiveOrZero(message = "Current stock must be a non-negative number")
    val currentStock: Int = 0,

    @field:PositiveOrZero(message = "Max stock must be a non-negative number")
    val maxStock: Int = 0,

    val status: MenuItemStatus = MenuItemStatus.AVAILABLE,

    @field:PositiveOrZero(message = "Preparation time must be a non-negative number")
    val preparationTime: Int? = null,

    @field:PositiveOrZero(message = "Calories must be a non-negative number")
    val calories: Int? = null,

    val allergens: List<String> = listOf(),
    val tags: List<String> = listOf()
)

data class UpdateMenuRequest(
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String?,

    @field:Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    val description: String?,

    val active: Boolean?
)

data class UpdateMenuItemRequest(
    @field:Size(min = 2, max = 100, message = "Menu item name must be between 2 and 100 characters")
    val name: String? = null,

    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,

    @field:Positive(message = "Price must be a positive number")
    val price: BigDecimal? = null,

    val category: MenuItemCategory? = null,
    val imageUrl: String? = null,

    @field:PositiveOrZero(message = "Current stock must be a non-negative number")
    val currentStock: Int? = null,

    @field:PositiveOrZero(message = "Max stock must be a non-negative number")
    val maxStock: Int? = null,

    val status: MenuItemStatus? = null,

    @field:PositiveOrZero(message = "Preparation time must be a non-negative number")
    val preparationTime: Int? = null,

    @field:PositiveOrZero(message = "Calories must be a non-negative number")
    val calories: Int? = null,

    val allergens: List<String>? = null,
    val tags: List<String>? = null
)

data class MenuSearchRequest(
    @field:Size(max = 100, message = "Search term must not exceed 100 characters")
    val name: String? = null,

    val restaurantId: String? = null,
    val allergens: Set<Allergen>? = null,
    val dietaryInfo: Set<DietaryInfo>? = null,
    val priceRange: PriceRange? = null,
    val available: Boolean? = null,

    @field:Min(value = 0, message = "Page number cannot be negative")
    val page: Int? = null,

    @field:Min(value = 1, message = "Page size must be at least 1")
    @field:Max(value = 100, message = "Page size must not exceed 100")
    val size: Int? = null
)

data class PriceRange(
    @field:DecimalMin(value = "0.0", message = "Minimum price cannot be negative")
    val min: BigDecimal,

    @field:DecimalMin(value = "0.0", message = "Maximum price cannot be negative")
    val max: BigDecimal
)

data class MenuPageResponse(
    val content: List<MenuDto>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int
) 