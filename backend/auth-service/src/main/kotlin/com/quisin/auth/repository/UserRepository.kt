package com.quisin.auth.repository

import com.quisin.auth.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
    fun findByRestaurantId(restaurantId: UUID): List<User>
}
