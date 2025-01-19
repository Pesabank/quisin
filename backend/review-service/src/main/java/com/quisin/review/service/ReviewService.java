package com.quisin.review.service;

import com.quisin.review.dto.ReviewRequest;
import com.quisin.review.dto.ReviewResponse;
import com.quisin.review.model.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, String userId);
    
    ReviewResponse updateReview(Long reviewId, ReviewRequest request, String userId);
    
    ReviewResponse getReview(Long reviewId);
    
    void deleteReview(Long reviewId, String userId);
    
    Page<ReviewResponse> getReviewsByRestaurant(String restaurantId, ReviewStatus status, Pageable pageable);
    
    Page<ReviewResponse> getReviewsByUser(String userId, ReviewStatus status, Pageable pageable);
    
    ReviewResponse moderateReview(Long reviewId, ReviewStatus newStatus, String moderatorId, String response);
    
    Double getAverageRating(String restaurantId);
    
    void processReviewModeration();
} 