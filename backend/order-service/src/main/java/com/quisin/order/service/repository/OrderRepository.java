package com.quisin.order.service.repository;

import com.quisin.order.service.model.Order;
import com.quisin.order.service.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<Order> findByRestaurantId(Long restaurantId, Pageable pageable);
    
    Page<Order> findByWaiterId(Long waiterId, Pageable pageable);
    
    List<Order> findByGroupOrderId(Long groupOrderId);
    
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.status IN :statuses")
    Page<Order> findByRestaurantIdAndStatusIn(Long restaurantId, List<OrderStatus> statuses, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.waiterId = :waiterId AND o.status IN :statuses")
    Page<Order> findByWaiterIdAndStatusIn(Long waiterId, List<OrderStatus> statuses, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate")
    Page<Order> findByRestaurantIdAndDateRange(Long restaurantId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    Optional<Order> findByIdAndRestaurantId(Long id, Long restaurantId);
    
    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);
} 