package com.adsearch.infrastructure.security.service

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.TokenService
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.security.model.JwtUserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for JWT token operations
 */
@Service
class JwtTokenService(
    private val tokenService: TokenService,
    private val userPersistencePort: UserPersistencePort
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(JwtTokenService::class.java)
    }

    /**
     * Generate a JWT token for a user
     */
    fun createAccessToken(user: JwtUserDetails): String {
        return tokenService.generateAccessToken(
            userId = user.id,
            username = user.username,
            roles = user.authorities.map { it.authority }
        )
    }

    /**
     * Create a refresh token for a user
     */
    fun createRefreshToken(user: JwtUserDetails): RefreshToken {
        LOG.debug("Creating refresh token for user: ${user.username} with ID: ${user.id}")
        return tokenService.generateRefreshToken(user.id)
    }

    /**
     * Validate a JWT token
     */
    fun validateAccessTokenAndGetUsername(token: String): String? {
        return tokenService.validateAccessToken(token)
    }

    /**
     * Validate a refresh token and get the username
     */
    fun validateRefreshTokenAndGetUsername(givenToken: String): String {
        val userId = tokenService.validateRefreshToken(givenToken)
        return userPersistencePort.findById(userId)?.username
            ?: throw RuntimeException("User not found for refresh token")
    }
}
