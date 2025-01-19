package com.quisin.user.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value("\${security.jwt.secret}")
    private val secretKey: String
) {
    private val signingKey = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(secretKey)
    )

    fun extractUsername(token: String): String? {
        return try {
            extractClaim(token) { obj: Claims -> obj.subject }
        } catch (e: Exception) {
            null
        }
    }

    fun extractRole(token: String): String? {
        return try {
            extractClaim(token) { obj: Claims -> obj["role"] as String }
        } catch (e: Exception) {
            null
        }
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            val claims = extractAllClaims(token)
            !isTokenExpired(claims)
        } catch (e: Exception) {
            false
        }
    }

    private fun isTokenExpired(claims: Claims): Boolean {
        return claims.expiration.before(Date())
    }

    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
} 