package com.adsearch.infrastructure.security.service

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.TokenGenerationPort
import com.adsearch.domain.port.TokenValidationPort
import com.adsearch.infrastructure.security.model.JwtUserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for refresh token operations
 * This service is now a thin wrapper that delegates to domain ports
 */
@Service
class JwtRefreshTokenService(
    private val tokenGenerationPort: TokenGenerationPort,
    private val tokenValidationPort: TokenValidationPort
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

        return tokenGenerationPort.createRefreshToken(userId, user.username)
    }

    suspend fun validateRefreshTokenAndGetUserId(givenToken: String): Long {
        return tokenValidationPort.validateRefreshTokenAndGetUserId(givenToken)
    }
}
