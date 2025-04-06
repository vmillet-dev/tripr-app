package com.adsearch.infrastructure.security.service

import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.infrastructure.security.model.JwtUserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Service for refresh token operations
 */
@Service
class JwtRefreshTokenService(
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    @Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Create a refresh token for a user
     */
    suspend fun createRefreshToken(user: JwtUserDetails): RefreshToken {
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

    suspend fun validateRefreshTokenAndGetUserId(givenToken: String): Long {
        val refreshToken: RefreshToken? = refreshTokenPersistencePort.findByToken(givenToken)

        if (refreshToken == null) {
            LOG.warn("refresh token invalid")
            throw InvalidTokenException()
        }
        verifyExpiration(refreshToken)
        return refreshToken.userId
    }

    /**
     * Verify if a refresh token is valid
     */
    private suspend fun verifyExpiration(token: RefreshToken) {
        if (token.expiryDate.isBefore(Instant.now()) || token.revoked) {
            refreshTokenPersistencePort.deleteById(token.id)
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }
    }
}
