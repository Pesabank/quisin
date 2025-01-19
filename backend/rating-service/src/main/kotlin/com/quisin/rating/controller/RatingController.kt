package com.quisin.rating.controller

import com.quisin.rating.dto.*
import com.quisin.rating.service.RatingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/ratings")
@Tag(name = "Rating Management", description = "APIs for managing ratings")
class RatingController(private val ratingService: RatingService) {

    @PostMapping
    @Operation(summary = "Create a new rating", description = "Submit a rating for a specific entity")
    fun createRating(
        @Valid @RequestBody request: CreateRatingRequest
    ): ResponseEntity<RatingResponse> {
        val response = ratingService.createRating(request)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{ratingId}")
    @Operation(summary = "Update an existing rating", description = "Modify details of a previously submitted rating")
    fun updateRating(
        @PathVariable ratingId: UUID,
        @Valid @RequestBody request: UpdateRatingRequest
    ): ResponseEntity<RatingResponse> {
        val response = ratingService.updateRating(ratingId, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{ratingId}")
    @Operation(summary = "Delete a rating", description = "Remove a previously submitted rating")
    fun deleteRating(
        @PathVariable ratingId: UUID
    ): ResponseEntity<Void> {
        ratingService.deleteRating(ratingId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/entity/{entityId}")
    @Operation(summary = "Get ratings for an entity", description = "Retrieve all ratings for a specific entity")
    fun getRatingsByEntity(
        @PathVariable entityId: UUID,
        @RequestParam type: RatingType
    ): ResponseEntity<List<RatingResponse>> {
        val ratings = ratingService.getRatingsByEntity(entityId, type)
        return ResponseEntity.ok(ratings)
    }

    @GetMapping("/summary/{entityId}")
    @Operation(summary = "Get rating summary", description = "Retrieve summary statistics for an entity")
    fun getRatingSummary(
        @PathVariable entityId: UUID,
        @RequestParam type: RatingType
    ): ResponseEntity<RatingSummaryResponse> {
        val summary = ratingService.getRatingSummary(entityId, type)
        return ResponseEntity.ok(summary)
    }

    @PostMapping("/analytics")
    @Operation(summary = "Get rating analytics", description = "Retrieve detailed rating analytics")
    fun getRatingAnalytics(
        @RequestBody request: RatingAnalyticsRequest
    ): ResponseEntity<List<RatingResponse>> {
        val analytics = ratingService.getAnalytics(request)
        return ResponseEntity.ok(analytics)
    }
}
