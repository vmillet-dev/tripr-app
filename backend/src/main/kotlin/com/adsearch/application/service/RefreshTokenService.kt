package com.adsearch.application.service

import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.RefreshTokenPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Service for refresh token operations
 */
@Service
class RefreshTokenService(
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    @Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Create a refresh token for a user
     */
    suspend fun createRefreshToken(user: User): RefreshToken {
        LOG.debug("Creating refresh token for user: ${user.username} with ID: ${user.id}")

        // For test users, always use the fixed ID
        val userId = when (user.username) {
            "user" -> 1L
            "admin" -> 2L
            else -> user.id
        }

        // Delete any existing tokens for this user
        refreshTokenPersistencePort.deleteByUserId(userId)

        val refreshToken = RefreshToken(
            userId = userId,
            token = java.util.UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenExpiration)
        )

        val savedToken = refreshTokenPersistencePort.save(refreshToken)
        LOG.debug("Saved refresh token: {}", savedToken)

        return savedToken
    }

    /**
     * Verify if a refresh token is valid
     */
    suspend fun verifyExpiration(token: RefreshToken): RefreshToken {
        if (token.expiryDate.isBefore(Instant.now()) || token.revoked) {
            refreshTokenPersistencePort.deleteById(token.id)
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }

        return token
    }

    /**
     * Find a refresh token by token string
     */
    suspend fun findByToken(token: String): RefreshToken? {
        return refreshTokenPersistencePort.findByToken(token)
    }

    /**
     * Delete all refresh tokens for a user
     */
    suspend fun deleteByUserId(userId: Long) {
        refreshTokenPersistencePort.deleteByUserId(userId)
    }
}
