package com.learning.spring_boot_learning.security

import com.learning.spring_boot_learning.database.model.RefreshToken
import com.learning.spring_boot_learning.database.model.User
import com.learning.spring_boot_learning.database.repository.RefreshTokenRepository
import com.learning.spring_boot_learning.database.repository.UserRepository
import com.learning.spring_boot_learning.security.model.TokenPair
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val hashEncoder: HashEncoder,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun register(email: String, password: String): User {
        val user = userRepository.findByEmail(email.trim())
        if (user != null) throw ResponseStatusException(HttpStatus.CONFLICT, "A user with that email already exists!")
        return userRepository.save(
            User(
                email,
                hashEncoder.encode(password)
            )
        )
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid credentials!")
        if (hashEncoder.matches(password, user.hashedPassword).not()) {
            throw BadCredentialsException("Invalid credentials!")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

                    // All actions related to updating the DB will be applied if all are succeeded, if one fails nothing will happen in DB
    @Transactional  // For example if storeRefreshToken() is failed deleteByUserIdAndHashedToken() will not be done as one of transactions is failed
    fun refresh(refreshToken: String): TokenPair {
        if (jwtService.validateRefreshToken(refreshToken).not())
            throw ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh token!")

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh token!")
        }
        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401),"Refresh token is not recognized (maybe used or expired)!")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expireMillis = jwtService.refreshTokenValidityMillis
        val expiresAt = Instant.now().plusMillis(expireMillis)
        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}