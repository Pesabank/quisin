package com.quisin.auth.service

import com.quisin.auth.domain.Role
import com.quisin.auth.domain.User
import com.quisin.auth.dto.AuthRequest
import com.quisin.auth.dto.RegisterRequest
import com.quisin.auth.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
class AuthenticationServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtService: JwtService

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    private lateinit var authenticationService: AuthenticationService

    @BeforeEach
    fun setUp() {
        authenticationService = AuthenticationService(
            userRepository,
            passwordEncoder,
            jwtService,
            authenticationManager
        )
    }

    @Test
    fun `register new user successfully`() {
        // Arrange
        val registerRequest = RegisterRequest(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            password = "password123",
            role = Role.ADMIN
        )

        `when`(userRepository.existsByEmail(registerRequest.email)).thenReturn(false)
        `when`(passwordEncoder.encode(registerRequest.password)).thenReturn("encodedPassword")
        `when`(jwtService.generateToken(any<User>())).thenReturn("accessToken")
        `when`(jwtService.generateRefreshToken(any<User>())).thenReturn("refreshToken")

        // Act
        val response = authenticationService.register(registerRequest)

        // Assert
        assertNotNull(response)
        assertEquals("accessToken", response.accessToken)
        assertEquals("refreshToken", response.refreshToken)

        // Verify interactions
        verify(userRepository).save(any<User>())
    }

    @Test
    fun `register with existing email throws exception`() {
        // Arrange
        val registerRequest = RegisterRequest(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            password = "password123",
            role = Role.ADMIN
        )

        `when`(userRepository.existsByEmail(registerRequest.email)).thenReturn(true)

        // Act & Assert
        assertThrows(IllegalStateException::class.java) {
            authenticationService.register(registerRequest)
        }
    }

    @Test
    fun `authenticate user successfully`() {
        // Arrange
        val authRequest = AuthRequest(
            email = "john.doe@example.com",
            password = "password123"
        )

        val user = User(
            email = authRequest.email,
            password = "encodedPassword",
            role = Role.ADMIN,
            firstName = "John",
            lastName = "Doe"
        )

        `when`(userRepository.findByEmail(authRequest.email)).thenReturn(Optional.of(user))
        `when`(jwtService.generateToken(user)).thenReturn("accessToken")
        `when`(jwtService.generateRefreshToken(user)).thenReturn("refreshToken")

        // Act
        val response = authenticationService.authenticate(authRequest)

        // Assert
        assertNotNull(response)
        assertEquals("accessToken", response.accessToken)
        assertEquals("refreshToken", response.refreshToken)
    }

    @Test
    fun `authenticate with non-existent user throws exception`() {
        // Arrange
        val authRequest = AuthRequest(
            email = "john.doe@example.com",
            password = "password123"
        )

        `when`(userRepository.findByEmail(authRequest.email)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(NoSuchElementException::class.java) {
            authenticationService.authenticate(authRequest)
        }
    }
}
