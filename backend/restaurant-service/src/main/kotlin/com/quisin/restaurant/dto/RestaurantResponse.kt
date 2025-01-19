package com.quisin.restaurant.dto

import com.quisin.restaurant.domain.RestaurantStatus
import java.util.*

data class RestaurantResponse(
    val id: UUID,
    val name: String,
    val address: String,
    val contactNumber: String,
    val email: String,
    val status: RestaurantStatus,
    val adminUserId: UUID,
    val description: String? = null,
    val websiteUrl: String? = null,
    val createdAt: Date,
    val updatedAt: Date? = null,
    val chainCount: Int = 0
)
