package com.learning.spring_boot_learning.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.Base64
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secret}") val jwtSecret: String
) {
    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMillis = 15L * 60L * 1000L
    val refreshTokenValidityMillis = 30L * 24L * 60L * 60L * 1000L

    fun generateAccessToken(userId: String): String = generateToken(
        userId,
        TOKEN_TYPE_ACCESS,
        accessTokenValidityMillis
    )

    fun generateRefreshToken(userId: String): String = generateToken(
        userId,
        TOKEN_TYPE_REFRESH,
        refreshTokenValidityMillis
    )

    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims[TOKEN_TYPE] as? String ?: return false
        return tokenType == TOKEN_TYPE_ACCESS
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims[TOKEN_TYPE] as? String ?: return false
        return tokenType == TOKEN_TYPE_REFRESH
    }

    fun getUserIdFromToken(token: String): String {
        val claims = parseAllClaims(token) ?: throw ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid token!")
        return claims.subject
    }

    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId)
            .claim(TOKEN_TYPE, type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = if (token.startsWith(BEARER)) {
            token.removePrefix(BEARER)
        } else token
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val TOKEN_TYPE = "type"
        private const val TOKEN_TYPE_ACCESS = "access"
        private const val TOKEN_TYPE_REFRESH = "refresh"
        const val BEARER = "Bearer "
    }
}