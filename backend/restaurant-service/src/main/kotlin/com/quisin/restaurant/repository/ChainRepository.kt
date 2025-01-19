package com.quisin.restaurant.repository

import com.quisin.restaurant.domain.Chain
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ChainRepository : JpaRepository<Chain, UUID>, JpaSpecificationExecutor<Chain> {
    fun findByOwnerId(ownerId: UUID): List<Chain>
    fun existsByNameAndOwnerId(name: String, ownerId: UUID): Boolean
    
    @Query("SELECT c FROM Chain c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Chain>
    
    fun findByOwnerIdOrderByCreatedAtDesc(ownerId: UUID, pageable: Pageable): Page<Chain>
    
    @Query("SELECT COUNT(r) FROM Chain c JOIN c.restaurants r WHERE c.id = :chainId")
    fun countRestaurants(chainId: UUID): Long
} 