package com.quisin.rating.dto

import com.quisin.rating.domain.RatingType
import com.quisin.rating.domain.RatingStatus
import jakarta.validation.constraints.*
import java.time.LocalDateTime
import java.util.UUID

data class CreateRatingRequest(
    @field:NotNull(message = "User ID is required")
    val userId: UUID,

    @field:NotNull(message = "Entity ID is required")
    val entityId: UUID,

    @field:NotNull(message = "Rating type is required")
    val type: RatingType,

    @field:Min(1, message = "Score must be at least 1")
    @field:Max(5, message = "Score must be at most 5")
    val score: Int,

    val comment: String? = null,

    val tags: List<String> = listOf()
)

data class UpdateRatingRequest(
    @field:Min(1, message = "Score must be at least 1")
    @field:Max(5, message = "Score must be at most 5")
    val score: Int? = null,

    val comment: String? = null,

    val tags: List<String>? = null,

    val status: RatingStatus? = null
)

data class RatingResponse(
    val id: UUID,
    val userId: UUID,
    val entityId: UUID,
    val type: RatingType,
    val score: Int,
    val comment: String?,
    val status: RatingStatus,
    val tags: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

data class RatingSummaryResponse(
    val entityId: UUID,
    val type: RatingType,
    val averageScore: Double,
    val totalRatings: Int,
    val weightedScore: Double
)

data class RatingAnalyticsRequest(
    val entityId: UUID,
    val type: RatingType,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)
