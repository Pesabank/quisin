package com.quisin.review.service.impl;

import com.quisin.review.dto.ReviewRequest;
import com.quisin.review.dto.ReviewResponse;
import com.quisin.review.model.Review;
import com.quisin.review.model.ReviewStatus;
import com.quisin.review.repository.ReviewRepository;
import com.quisin.review.service.ReviewService;
import com.quisin.review.validation.ReviewValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewValidator reviewValidator;

    @Value("${review.moderation.auto-approve:false}")
    private boolean autoApprove;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, String userId) {
        log.debug("Creating review for restaurant {} by user {}", request.getRestaurantId(), userId);
        
        reviewValidator.validateReview(request, userId);

        Review review = Review.builder()
            .restaurantId(request.getRestaurantId())
            .userId(userId)
            .orderId(request.getOrderId())
            .rating(request.getRating())
            .comment(request.getComment())
            .status(autoApprove ? ReviewStatus.APPROVED : ReviewStatus.PENDING)
            .visitDate(LocalDateTime.now()) // This should ideally come from the order service
            .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Created review with ID {} for restaurant {}", savedReview.getId(), request.getRestaurantId());
        
        return mapToResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, String userId) {
        log.debug("Updating review {} by user {}", reviewId, userId);
        
        Review review = getReviewByIdAndUserId(reviewId, userId);
        
        if (review.getStatus() == ReviewStatus.APPROVED) {
            review.setStatus(ReviewStatus.UPDATED);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        log.info("Updated review {}", reviewId);
        
        return mapToResponse(updatedReview);
    }

    @Override
    public ReviewResponse getReview(Long reviewId) {
        log.debug("Fetching review {}", reviewId);
        return mapToResponse(getReviewById(reviewId));
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, String userId) {
        log.debug("Deleting review {} by user {}", reviewId, userId);
        
        Review review = getReviewByIdAndUserId(reviewId, userId);
        reviewRepository.delete(review);
        
        log.info("Deleted review {}", reviewId);
    }

    @Override
    public Page<ReviewResponse> getReviewsByRestaurant(String restaurantId, ReviewStatus status, Pageable pageable) {
        log.debug("Fetching reviews for restaurant {} with status {}", restaurantId, status);
        return reviewRepository.findByRestaurantIdAndStatus(restaurantId, status, pageable)
            .map(this::mapToResponse);
    }

    @Override
    public Page<ReviewResponse> getReviewsByUser(String userId, ReviewStatus status, Pageable pageable) {
        log.debug("Fetching reviews by user {} with status {}", userId, status);
        return reviewRepository.findByUserIdAndStatus(userId, status, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ReviewResponse moderateReview(Long reviewId, ReviewStatus newStatus, String moderatorId, String response) {
        log.debug("Moderating review {} to status {} by moderator {}", reviewId, newStatus, moderatorId);
        
        Review review = getReviewById(reviewId);
        review.setStatus(newStatus);
        review.setModeratorId(moderatorId);
        review.setModeratedAt(LocalDateTime.now());
        
        if (response != null && !response.trim().isEmpty()) {
            review.setResponse(response);
        }

        Review moderatedReview = reviewRepository.save(review);
        log.info("Moderated review {} to status {}", reviewId, newStatus);
        
        return mapToResponse(moderatedReview);
    }

    @Override
    public Double getAverageRating(String restaurantId) {
        log.debug("Calculating average rating for restaurant {}", restaurantId);
        return reviewRepository.getAverageRatingForRestaurant(restaurantId);
    }

    @Override
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void processReviewModeration() {
        log.debug("Processing review moderation");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        var pendingReviews = reviewRepository.findPendingReviewsOlderThan(cutoffTime);
        
        for (Review review : pendingReviews) {
            review.setStatus(ReviewStatus.APPROVED);
            reviewRepository.save(review);
            log.info("Auto-approved review {} after 24 hours", review.getId());
        }
    }

    private Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
            .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));
    }

    private Review getReviewByIdAndUserId(Long reviewId, String userId) {
        Review review = getReviewById(reviewId);
        if (!Objects.equals(review.getUserId(), userId)) {
            throw new IllegalStateException("User does not own this review");
        }
        return review;
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
            .id(review.getId())
            .restaurantId(review.getRestaurantId())
            .userId(review.getUserId())
            .orderId(review.getOrderId())
            .rating(review.getRating())
            .comment(review.getComment())
            .response(review.getResponse())
            .status(review.getStatus())
            .visitDate(review.getVisitDate())
            .createdAt(review.getCreatedAt())
            .updatedAt(review.getUpdatedAt())
            .build();
    }
} 