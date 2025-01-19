package com.quisin.restaurant.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class ChainDto(
    val id: UUID?,
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,
    @field:NotBlank(message = "Description is required")
    @field:Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    val description: String,
    @field:NotNull(message = "Restaurant IDs set is required")
    val restaurantIds: Set<UUID>,
    @field:NotNull(message = "Owner ID is required")
    val ownerId: UUID,
    @field:NotNull(message = "Created timestamp is required")
    val createdAt: LocalDateTime,
    @field:NotNull(message = "Updated timestamp is required")
    val updatedAt: LocalDateTime
)

data class CreateChainRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,
    @field:NotBlank(message = "Description is required")
    @field:Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    val description: String
)

data class UpdateChainRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,
    @field:NotBlank(message = "Description is required")
    @field:Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    val description: String
)

data class ChainResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val restaurantCount: Int
) 