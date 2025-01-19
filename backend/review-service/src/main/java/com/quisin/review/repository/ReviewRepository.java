package com.quisin.review.repository;

import com.quisin.review.model.Review;
import com.quisin.review.model.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByRestaurantIdAndStatus(String restaurantId, ReviewStatus status, Pageable pageable);
    
    Page<Review> findByUserIdAndStatus(String userId, ReviewStatus status, Pageable pageable);
    
    List<Review> findByOrderId(String orderId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.userId = :userId AND r.createdAt >= :since")
    int countReviewsByUserSince(@Param("userId") String userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurantId = :restaurantId AND r.status = 'APPROVED'")
    Double getAverageRatingForRestaurant(@Param("restaurantId") String restaurantId);
    
    @Query("SELECT r.rating, COUNT(r) FROM Review r " +
           "WHERE r.restaurantId = :restaurantId AND r.status = 'APPROVED' " +
           "GROUP BY r.rating")
    List<Object[]> getRatingDistributionForRestaurant(@Param("restaurantId") String restaurantId);
    
    boolean existsByOrderIdAndStatus(String orderId, ReviewStatus status);
    
    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.status = 'PENDING' AND r.createdAt <= :cutoffTime")
    List<Review> findPendingReviewsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(r) > 0 FROM Review r " +
           "WHERE r.orderId = :orderId AND r.userId = :userId AND r.status <> 'REJECTED'")
    boolean hasUserReviewedOrder(@Param("orderId") String orderId, @Param("userId") String userId);
} 