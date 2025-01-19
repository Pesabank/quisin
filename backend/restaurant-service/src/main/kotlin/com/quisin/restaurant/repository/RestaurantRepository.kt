package com.quisin.restaurant.repository

import com.quisin.restaurant.domain.Restaurant
import com.quisin.restaurant.domain.RestaurantStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantRepository : JpaRepository<Restaurant, UUID>, JpaSpecificationExecutor<Restaurant> {
    fun existsByNameAndOwnerId(name: String, ownerId: UUID): Boolean
    fun findByOwnerId(ownerId: UUID): List<Restaurant>
    fun findByChainId(chainId: UUID): List<Restaurant>
    fun findByStatus(status: RestaurantStatus): List<Restaurant>
    
    @Query("SELECT r FROM Restaurant r WHERE r.location.city = :city")
    fun findByCity(city: String, pageable: Pageable): Page<Restaurant>
    
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Restaurant>
    
    @Query("SELECT r FROM Restaurant r WHERE r.chain.id = :chainId")
    fun findByChainIdWithPagination(chainId: UUID, pageable: Pageable): Page<Restaurant>
    
    @Query("SELECT COUNT(r) > 0 FROM Restaurant r WHERE r.chain.id = :chainId")
    fun existsByChainId(chainId: UUID): Boolean
}
