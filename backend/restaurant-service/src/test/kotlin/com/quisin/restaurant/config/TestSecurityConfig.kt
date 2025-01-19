package com.quisin.restaurant.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import java.security.KeyPair
import java.security.KeyPairGenerator

@TestConfiguration
class TestSecurityConfig {

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val keyPair = generateRsaKey()
        return NimbusJwtDecoder.withPublicKey(keyPair.public as java.security.interfaces.RSAPublicKey).build()
    }

    private fun generateRsaKey(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }
} 