package com.quisin.menu.repository

import com.quisin.menu.domain.Menu
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MenuRepository : JpaRepository<Menu, String>, JpaSpecificationExecutor<Menu> {
    fun findByRestaurantId(restaurantId: String): List<Menu>
    fun findByRestaurantIdAndActive(restaurantId: String, active: Boolean): List<Menu>
    fun countByActive(active: Boolean): Long
    
    @Query("SELECT m FROM Menu m WHERE m.restaurantId = :restaurantId AND m.active = true")
    fun findActiveByRestaurantId(restaurantId: String, pageable: Pageable): Page<Menu>
    
    @Query("SELECT m FROM Menu m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Menu>
    
    @Query("""
        SELECT DISTINCT m FROM Menu m
        JOIN m.categories c
        JOIN c.items i
        WHERE i.price BETWEEN :minPrice AND :maxPrice
    """)
    fun findByPriceRange(minPrice: java.math.BigDecimal, maxPrice: java.math.BigDecimal, pageable: Pageable): Page<Menu>
    
    @Query("""
        SELECT DISTINCT m FROM Menu m
        JOIN m.categories c
        JOIN c.items i
        WHERE :allergen MEMBER OF i.allergens
    """)
    fun findByAllergen(allergen: String, pageable: Pageable): Page<Menu>
    
    @Query("""
        SELECT DISTINCT m FROM Menu m
        JOIN m.categories c
        JOIN c.items i
        WHERE :dietaryInfo MEMBER OF i.dietaryInfo
    """)
    fun findByDietaryInfo(dietaryInfo: String, pageable: Pageable): Page<Menu>
} 