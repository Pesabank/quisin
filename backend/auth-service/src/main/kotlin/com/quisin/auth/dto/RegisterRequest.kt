package com.quisin.auth.dto

import com.quisin.auth.domain.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class RegisterRequest(
    @field:NotBlank(message = "First name is required")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    val lastName: String,

    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val password: String,

    val role: Role,
    
    val restaurantId: UUID? = null
)
