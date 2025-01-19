package com.quisin.user.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

enum class UserRole {
    ADMIN, MANAGER, STAFF, OWNER
}

enum class UserStatus {
    ACTIVE, INACTIVE, SUSPENDED, PENDING
}

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: UserStatus = UserStatus.PENDING,

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column
    val phoneNumber: String? = null,

    @Column
    val restaurantId: UUID? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    val updatedAt: LocalDateTime? = null
)
