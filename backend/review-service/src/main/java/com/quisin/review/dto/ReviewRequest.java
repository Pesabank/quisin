package com.quisin.review.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    @Size(min = 10, max = 1000, message = "Comment must be between 10 and 1000 characters")
    private String comment;
} 