package com.quisin.user.controller

import com.quisin.user.domain.UserRole
import com.quisin.user.domain.UserStatus
import com.quisin.user.dto.*
import com.quisin.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Operations for managing users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    @Operation(summary = "Create a new user", description = "Register a new user in the system")
    fun createUser(
        @Valid @RequestBody request: UserCreateRequest
    ): ResponseEntity<UserResponse> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user's details by their unique identifier")
    fun getUserById(
        @PathVariable userId: UUID
    ): ResponseEntity<UserResponse> {
        val user = userService.getUserById(userId)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieve a user's details by their username")
    fun getUserByUsername(
        @PathVariable username: String
    ): ResponseEntity<UserResponse> {
        val user = userService.getUserByUsername(username)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user details", description = "Update a user's profile information")
    fun updateUser(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: UserUpdateRequest
    ): ResponseEntity<UserResponse> {
        val user = userService.updateUser(userId, request)
        return ResponseEntity.ok(user)
    }

    @PatchMapping("/{userId}/credentials")
    @Operation(summary = "Update user credentials", description = "Change a user's password")
    fun updateUserCredentials(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: UserCredentialsUpdateRequest
    ): ResponseEntity<Void> {
        userService.updateUserCredentials(userId, request)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "Change user status", description = "Update a user's account status")
    fun changeUserStatus(
        @PathVariable userId: UUID,
        @RequestParam status: UserStatus
    ): ResponseEntity<UserResponse> {
        val user = userService.changeUserStatus(userId, status)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieve all users with a specific role")
    fun getUsersByRole(
        @PathVariable role: UserRole
    ): ResponseEntity<List<UserResponse>> {
        val users = userService.getUsersByRole(role)
        return ResponseEntity.ok(users)
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get users by restaurant", description = "Retrieve all users associated with a specific restaurant")
    fun getUsersByRestaurant(
        @PathVariable restaurantId: UUID
    ): ResponseEntity<List<UserResponse>> {
        val users = userService.getUsersByRestaurant(restaurantId)
        return ResponseEntity.ok(users)
    }
}
