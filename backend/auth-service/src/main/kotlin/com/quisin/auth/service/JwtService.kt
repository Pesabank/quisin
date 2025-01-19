package com.quisin.auth.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val secretKey: String,
    
    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long,
    
    @Value("\${jwt.refresh-token.expiration}")
    private val refreshExpiration: Long
) {
    private val signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey))

    fun extractUsername(token: String): String {
        return extractClaim(token) { obj: Claims -> obj.subject }
    }

    fun generateToken(userDetails: UserDetails): String {
        return generateToken(HashMap(), userDetails)
    }

    fun generateToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails
    ): String {
        return buildToken(extraClaims, userDetails, jwtExpiration)
    }

    fun generateRefreshToken(
        userDetails: UserDetails
    ): String {
        return buildToken(HashMap(), userDetails, refreshExpiration)
    }

    private fun buildToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
        expiration: Long
    ): String {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username) && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
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
