package com.quisin.user.repository

import com.quisin.user.domain.User
import com.quisin.user.domain.UserRole
import com.quisin.user.domain.UserStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsername(username: String): Optional<User>
    fun findByEmail(email: String): Optional<User>
    fun findByRestaurantId(restaurantId: UUID): List<User>
    fun findByRole(role: UserRole): List<User>
    fun findByStatus(status: UserStatus): List<User>

    @Query("SELECT u FROM User u WHERE u.restaurantId = :restaurantId AND u.role = :role")
    fun findByRestaurantIdAndRole(restaurantId: UUID, role: UserRole): List<User>

    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}
