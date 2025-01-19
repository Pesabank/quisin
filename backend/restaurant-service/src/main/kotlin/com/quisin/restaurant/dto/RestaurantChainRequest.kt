package com.quisin.restaurant.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class RestaurantChainRequest(
    @field:NotBlank(message = "Chain name is required")
    @field:Size(min = 2, max = 100, message = "Chain name must be between 2 and 100 characters")
    val name: String,

    @field:NotBlank(message = "Location is required")
    val location: String,

    val restaurantId: UUID,

    val description: String? = null
)
