package com.quisin.review.controller;

import com.quisin.review.dto.ReviewRequest;
import com.quisin.review.dto.ReviewResponse;
import com.quisin.review.model.ReviewStatus;
import com.quisin.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Management", description = "APIs for managing restaurant reviews")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a new review")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt
    ) {
        ReviewResponse response = reviewService.createReview(request, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update an existing review")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt
    ) {
        ReviewResponse response = reviewService.updateReview(reviewId, request, jwt.getSubject());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get a review by ID")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        ReviewResponse response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(
            @PathVariable Long reviewId,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt
    ) {
        reviewService.deleteReview(reviewId, jwt.getSubject());
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get reviews for a restaurant")
    public ResponseEntity<Page<ReviewResponse>> getRestaurantReviews(
            @PathVariable String restaurantId,
            @RequestParam(required = false, defaultValue = "APPROVED") ReviewStatus status,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ReviewResponse> reviews = reviewService.getReviewsByRestaurant(restaurantId, status, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user")
    @Operation(summary = "Get reviews by the current user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ReviewResponse>> getUserReviews(
            @RequestParam(required = false) ReviewStatus status,
            @PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt
    ) {
        Page<ReviewResponse> reviews = reviewService.getReviewsByUser(jwt.getSubject(), status, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PatchMapping("/{reviewId}/moderate")
    @Operation(summary = "Moderate a review")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ReviewResponse> moderateReview(
            @PathVariable Long reviewId,
            @RequestParam ReviewStatus status,
            @RequestParam(required = false) String response,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt
    ) {
        ReviewResponse moderatedReview = reviewService.moderateReview(reviewId, status, jwt.getSubject(), response);
        return ResponseEntity.ok(moderatedReview);
    }

    @GetMapping("/restaurant/{restaurantId}/rating")
    @Operation(summary = "Get average rating for a restaurant")
    public ResponseEntity<Double> getRestaurantRating(@PathVariable String restaurantId) {
        Double averageRating = reviewService.getAverageRating(restaurantId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }
} 