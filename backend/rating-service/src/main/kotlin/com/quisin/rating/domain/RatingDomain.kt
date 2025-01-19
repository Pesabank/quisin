package com.quisin.rating.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class RatingType {
    RESTAURANT,
    MENU_ITEM,
    SERVICE,
    OVERALL_EXPERIENCE
}

enum class RatingStatus {
    ACTIVE,
    HIDDEN,
    REPORTED
}

@Entity
@Table(name = "ratings")
@EntityListeners(AuditingEntityListener::class)
data class Rating(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val entityId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: RatingType,

    @Column(nullable = false)
    val score: Int,

    @Column(length = 1000)
    val comment: String? = null,

    @Enumerated(EnumType.STRING)
    var status: RatingStatus = RatingStatus.ACTIVE,

    @ElementCollection
    @CollectionTable(name = "rating_tags", joinColumns = [JoinColumn(name = "rating_id")])
    @Column(name = "tag")
    val tags: List<String> = listOf(),

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)

@Entity
@Table(name = "rating_summaries")
data class RatingSummary(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val entityId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: RatingType,

    @Column(nullable = false)
    var averageScore: Double = 0.0,

    @Column(nullable = false)
    var totalRatings: Int = 0,

    @Column(nullable = false)
    var weightedScore: Double = 0.0
)
