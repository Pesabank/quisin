package com.quisin.qrcode.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant_tables")
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String restaurantId;

    @Column(nullable = false, unique = true)
    private String tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    private String description;

    @Column(nullable = false)
    private String qrCodeUrl;

    @Column(nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
} 