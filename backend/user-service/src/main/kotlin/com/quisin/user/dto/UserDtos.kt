package com.quisin.user.dto

import com.quisin.user.domain.UserRole
import com.quisin.user.domain.UserStatus
import jakarta.validation.constraints.*
import java.time.LocalDateTime
import java.util.*

data class UserCreateRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val password: String,

    @field:NotBlank(message = "First name is required")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    val lastName: String,

    val phoneNumber: String? = null,

    @field:NotNull(message = "User role is required")
    val role: UserRole,

    val restaurantId: UUID? = null
)

data class UserUpdateRequest(
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String? = null,

    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val status: UserStatus? = null
)

data class UserResponse(
    val id: UUID,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val role: UserRole,
    val status: UserStatus,
    val restaurantId: UUID?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class UserCredentialsUpdateRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,

    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "New password must be at least 8 characters long")
    val newPassword: String
)
