package com.quisin.auth.controller

import com.quisin.auth.dto.AuthRequest
import com.quisin.auth.dto.AuthResponse
import com.quisin.auth.dto.RegisterRequest
import com.quisin.auth.service.AuthenticationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException

@RestController
@RequestMapping("/api/auth", produces = [MediaType.APPLICATION_JSON_VALUE])
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080"])
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {
    private val logger = LoggerFactory.getLogger(AuthenticationController::class.java)

    @GetMapping("/health")
    fun healthCheck() = ResponseEntity.ok(mapOf("status" to "UP"))

    @PostMapping("/register", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody @Valid request: RegisterRequest): ResponseEntity<AuthResponse> {
        logger.debug("Processing registration for user: ${request.email}")
        return ResponseEntity.ok(authenticationService.register(request))
    }

    @PostMapping("/authenticate", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun authenticate(@RequestBody @Valid request: AuthRequest): ResponseEntity<AuthResponse> {
        logger.debug("Processing authentication for user: ${request.email}")
        return ResponseEntity.ok(authenticationService.authenticate(request))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestHeader("Authorization") authHeader: String): ResponseEntity<AuthResponse> {
        val token = authHeader.takeIf { it.startsWith("Bearer ") }
            ?.substring(7)
            ?: throw IllegalArgumentException("Invalid authorization header")
        
        return ResponseEntity.ok(authenticationService.refreshToken(token))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<Map<String, String>> {
        logger.warn("Authentication failed: Invalid credentials")
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("error" to "Invalid email or password"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors = ex.bindingResult.fieldErrors.associate { 
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("errors" to errors.toString()))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericError(ex: Exception): ResponseEntity<Map<String, String>> {
        logger.error("Unexpected error during authentication", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "An unexpected error occurred"))
    }
}
