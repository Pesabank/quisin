package com.quisin.rating.repository

import com.quisin.rating.domain.Rating
import com.quisin.rating.domain.RatingStatus
import com.quisin.rating.domain.RatingType
import com.quisin.rating.domain.RatingSummary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface RatingRepository : JpaRepository<Rating, UUID> {
    fun findByEntityIdAndType(entityId: UUID, type: RatingType): List<Rating>
    
    fun findByUserIdAndEntityId(userId: UUID, entityId: UUID): Rating?
    
    fun findByUserIdAndEntityIdAndType(userId: UUID, entityId: UUID, type: RatingType): Rating?
    
    fun findByTypeAndStatus(type: RatingType, status: RatingStatus): List<Rating>
    
    @Query("""
        SELECT r FROM Rating r 
        WHERE r.entityId = :entityId 
        AND r.type = :type 
        AND r.createdAt BETWEEN :startDate AND :endDate
    """)
    fun findByEntityIdAndTypeAndDateRange(
        entityId: UUID, 
        type: RatingType, 
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): List<Rating>
}

@Repository
interface RatingSummaryRepository : JpaRepository<RatingSummary, UUID> {
    fun findByEntityIdAndType(entityId: UUID, type: RatingType): RatingSummary?
    
    @Query("""
        SELECT rs FROM RatingSummary rs 
        WHERE rs.type = :type 
        ORDER BY rs.weightedScore DESC
    """)
    fun findTopRatedByType(type: RatingType, limit: Int): List<RatingSummary>
}
