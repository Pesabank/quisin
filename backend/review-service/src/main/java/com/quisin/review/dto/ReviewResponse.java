package com.quisin.review.dto;

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
public class ReviewResponse {
    private Long id;
    private String restaurantId;
    private String userId;
    private String orderId;
    private Integer rating;
    private String comment;
    private String response;
    private ReviewStatus status;
    private LocalDateTime visitDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 