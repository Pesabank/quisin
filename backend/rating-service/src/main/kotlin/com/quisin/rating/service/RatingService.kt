package com.quisin.rating.service

import com.quisin.rating.domain.Rating
import com.quisin.rating.domain.RatingStatus
import com.quisin.rating.domain.RatingSummary
import com.quisin.rating.dto.*
import com.quisin.rating.repository.RatingRepository
import com.quisin.rating.repository.RatingSummaryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class RatingService(
    private val ratingRepository: RatingRepository,
    private val ratingSummaryRepository: RatingSummaryRepository
) {
    @Transactional
    fun createRating(request: CreateRatingRequest): RatingResponse {
        // Check if user has already rated this entity
        val existingRating = ratingRepository.findByUserIdAndEntityIdAndType(
            request.userId, 
            request.entityId, 
            request.type
        )
        
        if (existingRating != null) {
            throw RatingException("User has already rated this entity")
        }

        // Create new rating
        val rating = Rating(
            userId = request.userId,
            entityId = request.entityId,
            type = request.type,
            score = request.score,
            comment = request.comment,
            tags = request.tags
        )

        val savedRating = ratingRepository.save(rating)

        // Update or create rating summary
        updateRatingSummary(savedRating)

        return mapToRatingResponse(savedRating)
    }

    @Transactional
    fun updateRating(ratingId: UUID, request: UpdateRatingRequest): RatingResponse {
        val rating = ratingRepository.findById(ratingId)
            .orElseThrow { RatingException("Rating not found") }

        // Update rating fields
        request.score?.let { rating.score = it }
        request.comment?.let { rating.comment = it }
        request.tags?.let { rating.tags = it }
        request.status?.let { rating.status = it }

        val updatedRating = ratingRepository.save(rating)
        updateRatingSummary(updatedRating)

        return mapToRatingResponse(updatedRating)
    }

    @Transactional
    fun deleteRating(ratingId: UUID) {
        val rating = ratingRepository.findById(ratingId)
            .orElseThrow { RatingException("Rating not found") }

        ratingRepository.delete(rating)
        updateRatingSummary(rating)
    }

    fun getRatingsByEntity(entityId: UUID, type: RatingType): List<RatingResponse> {
        return ratingRepository.findByEntityIdAndType(entityId, type)
            .map { mapToRatingResponse(it) }
    }

    fun getRatingSummary(entityId: UUID, type: RatingType): RatingSummaryResponse {
        return ratingSummaryRepository.findByEntityIdAndType(entityId, type)
            ?.let { mapToRatingSummaryResponse(it) }
            ?: RatingSummaryResponse(entityId, type, 0.0, 0, 0.0)
    }

    fun getAnalytics(request: RatingAnalyticsRequest): List<RatingResponse> {
        val startDate = request.startDate ?: LocalDateTime.now().minusMonths(1)
        val endDate = request.endDate ?: LocalDateTime.now()

        return ratingRepository.findByEntityIdAndTypeAndDateRange(
            request.entityId, 
            request.type, 
            startDate, 
            endDate
        ).map { mapToRatingResponse(it) }
    }

    private fun updateRatingSummary(rating: Rating) {
        val existingSummary = ratingSummaryRepository.findByEntityIdAndType(rating.entityId, rating.type)
            ?: RatingSummary(entityId = rating.entityId, type = rating.type)

        val ratings = ratingRepository.findByEntityIdAndType(rating.entityId, rating.type)
        
        existingSummary.totalRatings = ratings.size
        existingSummary.averageScore = ratings.map { it.score }.average().orElse(0.0)
        
        // Implement weighted score calculation (example: recency-based weighting)
        existingSummary.weightedScore = calculateWeightedScore(ratings)

        ratingSummaryRepository.save(existingSummary)
    }

    private fun calculateWeightedScore(ratings: List<Rating>): Double {
        // Simple weighted score calculation based on recency and score
        return ratings.map { rating ->
            val recencyFactor = LocalDateTime.now().minusWeeks(2).isBefore(rating.createdAt)
            val scoreFactor = rating.score.toDouble()
            if (recencyFactor) scoreFactor * 1.2 else scoreFactor
        }.average().orElse(0.0)
    }

    private fun mapToRatingResponse(rating: Rating): RatingResponse {
        return RatingResponse(
            id = rating.id!!,
            userId = rating.userId,
            entityId = rating.entityId,
            type = rating.type,
            score = rating.score,
            comment = rating.comment,
            status = rating.status,
            tags = rating.tags,
            createdAt = rating.createdAt!!,
            updatedAt = rating.updatedAt
        )
    }

    private fun mapToRatingSummaryResponse(summary: RatingSummary): RatingSummaryResponse {
        return RatingSummaryResponse(
            entityId = summary.entityId,
            type = summary.type,
            averageScore = summary.averageScore,
            totalRatings = summary.totalRatings,
            weightedScore = summary.weightedScore
        )
    }
}

class RatingException(message: String) : RuntimeException(message)
