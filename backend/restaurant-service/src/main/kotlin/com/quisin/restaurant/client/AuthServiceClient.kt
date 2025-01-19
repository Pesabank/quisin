package com.quisin.restaurant.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "auth-service", url = "\${AUTH_SERVICE_URL:http://auth-service:8081}")
interface AuthServiceClient {

    @GetMapping("/api/v1/auth/validate")
    fun validateToken(@RequestHeader("Authorization") token: String): TokenValidationResponse

    @GetMapping("/api/v1/auth/permissions/{userId}")
    fun getUserPermissions(@PathVariable userId: String): PermissionsResponse
}

data class TokenValidationResponse(
    val valid: Boolean,
    val userId: String?,
    val roles: Set<String>?,
    val error: String?
)

data class PermissionsResponse(
    val userId: String,
    val roles: Set<String>,
    val permissions: Set<String>
) 