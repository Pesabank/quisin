package com.quisin.restaurant.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class RestaurantRequest(
    @field:NotBlank(message = "Restaurant name is required")
    @field:Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    val name: String,

    @field:NotBlank(message = "Address is required")
    val address: String,

    @field:NotBlank(message = "Contact number is required")
    @field:Size(min = 10, max = 15, message = "Contact number must be between 10 and 15 characters")
    val contactNumber: String,

    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,

    val adminUserId: UUID,

    val description: String? = null,
    val websiteUrl: String? = null
)
