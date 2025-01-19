package com.quisin.restaurant.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDateTime
import java.util.UUID

@FeignClient(name = "user-service", url = "\${USER_SERVICE_URL:http://user-service:8082}")
interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    fun getUser(@PathVariable userId: UUID): UserResponse
}

data class UserResponse(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val roles: Set<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 