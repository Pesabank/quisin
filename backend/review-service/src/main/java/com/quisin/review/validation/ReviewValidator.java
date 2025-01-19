package com.quisin.review.validation;

import com.quisin.review.dto.ReviewRequest;
import com.quisin.review.exception.ReviewValidationException;
import com.quisin.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReviewValidator {
    private final ReviewRepository reviewRepository;

    @Value("${review.moderation.forbidden-words:}")
    private Set<String> forbiddenWords;

    @Value("${review.moderation.max-reviews-per-user-per-day:5}")
    private int maxReviewsPerUserPerDay;

    public void validateReview(ReviewRequest request, String userId) {
        List<String> validationErrors = new ArrayList<>();

        validateUserReviewLimit(userId, validationErrors);
        validateDuplicateReview(request.getOrderId(), userId, validationErrors);
        validateContent(request.getComment(), validationErrors);

        if (!validationErrors.isEmpty()) {
            throw new ReviewValidationException(String.join(", ", validationErrors));
        }
    }

    private void validateUserReviewLimit(String userId, List<String> errors) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        int dailyReviews = reviewRepository.countReviewsByUserSince(userId, startOfDay);
        
        if (dailyReviews >= maxReviewsPerUserPerDay) {
            errors.add("Maximum daily review limit reached");
        }
    }

    private void validateDuplicateReview(String orderId, String userId, List<String> errors) {
        if (reviewRepository.hasUserReviewedOrder(orderId, userId)) {
            errors.add("You have already reviewed this order");
        }
    }

    private void validateContent(String comment, List<String> errors) {
        if (comment != null && !comment.trim().isEmpty()) {
            for (String word : forbiddenWords) {
                if (comment.toLowerCase().contains(word.toLowerCase())) {
                    errors.add("Review contains inappropriate content");
                    break;
                }
            }
        }
    }
} 