package com.quisin.auth.service

import com.quisin.auth.domain.User
import com.quisin.auth.dto.AuthRequest
import com.quisin.auth.dto.AuthResponse
import com.quisin.auth.dto.RegisterRequest
import com.quisin.auth.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalStateException("Email already exists")
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = request.role,
            firstName = request.firstName,
            lastName = request.lastName,
            restaurantId = request.restaurantId
        )

        userRepository.save(user)

        val accessToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun authenticate(request: AuthRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        val user = userRepository.findByEmail(request.email)
            .orElseThrow { NoSuchElementException("User not found") }
        
        val accessToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        val username = jwtService.extractUsername(refreshToken)
        val user = userRepository.findByEmail(username)
            .orElseThrow { NoSuchElementException("User not found") }

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw IllegalStateException("Invalid refresh token")
        }

        val accessToken = jwtService.generateToken(user)
        
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}
