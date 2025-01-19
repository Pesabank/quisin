package com.quisin.qrcode.repository;

import com.quisin.qrcode.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByRestaurantId(String restaurantId);
    Optional<RestaurantTable> findByTableNumberAndRestaurantId(String tableNumber, String restaurantId);
    boolean existsByTableNumberAndRestaurantId(String tableNumber, String restaurantId);
    
    @Query("SELECT t.tableNumber FROM RestaurantTable t WHERE t.restaurantId = ?1 ORDER BY t.createdAt DESC")
    List<String> findLastTableNumberByRestaurantId(String restaurantId);
} 