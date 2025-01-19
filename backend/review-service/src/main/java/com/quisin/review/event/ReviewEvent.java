package com.quisin.review.event;

import com.quisin.review.model.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEvent {
    private String eventId;
    private String eventType;
    private Long reviewId;
    private String restaurantId;
    private String userId;
    private String orderId;
    private Integer rating;
    private ReviewStatus status;
    private LocalDateTime timestamp;
} 