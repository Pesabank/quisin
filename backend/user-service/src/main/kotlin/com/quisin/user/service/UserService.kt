package com.quisin.user.service

import com.quisin.user.domain.User
import com.quisin.user.domain.UserRole
import com.quisin.user.domain.UserStatus
import com.quisin.user.dto.*
import com.quisin.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun createUser(request: UserCreateRequest): UserResponse {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalStateException("Username already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalStateException("Email already exists")
        }

        // Create new user
        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = request.role,
            firstName = request.firstName,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            restaurantId = request.restaurantId,
            status = UserStatus.PENDING
        )

        val savedUser = userRepository.save(user)
        return mapToUserResponse(savedUser)
    }

    @Transactional(readOnly = true)
    fun getUserById(userId: UUID): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User not found") }
        return mapToUserResponse(user)
    }

    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): UserResponse {
        val user = userRepository.findByUsername(username)
            .orElseThrow { NoSuchElementException("User not found") }
        return mapToUserResponse(user)
    }

    @Transactional
    fun updateUser(userId: UUID, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User not found") }

        // Update mutable fields
        val updatedUser = user.copy(
            username = request.username ?: user.username,
            firstName = request.firstName ?: user.firstName,
            lastName = request.lastName ?: user.lastName,
            phoneNumber = request.phoneNumber ?: user.phoneNumber,
            status = request.status ?: user.status
        )

        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }

    @Transactional
    fun updateUserCredentials(userId: UUID, request: UserCredentialsUpdateRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User not found") }

        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw SecurityException("Current password is incorrect")
        }

        // Update password
        val updatedUser = user.copy(
            password = passwordEncoder.encode(request.newPassword)
        )

        userRepository.save(updatedUser)
    }

    @Transactional
    fun changeUserStatus(userId: UUID, status: UserStatus): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User not found") }

        val updatedUser = user.copy(status = status)
        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }

    @Transactional(readOnly = true)
    fun getUsersByRole(role: UserRole): List<UserResponse> {
        return userRepository.findByRole(role).map { mapToUserResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getUsersByRestaurant(restaurantId: UUID): List<UserResponse> {
        return userRepository.findByRestaurantId(restaurantId).map { mapToUserResponse(it) }
    }

    private fun mapToUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id!!,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            phoneNumber = user.phoneNumber,
            role = user.role,
            status = user.status,
            restaurantId = user.restaurantId,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}
