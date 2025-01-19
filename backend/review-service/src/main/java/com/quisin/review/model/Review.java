package com.quisin.review.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String restaurantId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @Column(length = 1000)
    private String response;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @Column(nullable = false)
    private LocalDateTime visitDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String moderatorId;

    private LocalDateTime moderatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ReviewStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 