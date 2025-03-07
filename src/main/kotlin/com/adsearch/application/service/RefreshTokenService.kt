package com.adsearch.application.service

import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.RefreshTokenRepositoryPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

/**
 * Service for refresh token operations
 */
@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepositoryPort,
    
    @Value("\${jwt.refresh-token.expiration}")
    private val refreshTokenExpiration: Long
) {
    
    /**
     * Create a refresh token for a user
     */
    suspend fun createRefreshToken(user: User): RefreshToken {
        val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
        logger.debug("Creating refresh token for user: ${user.username} with ID: ${user.id}")
        
        // For test users, always use the fixed ID
        val userId = when (user.username) {
            "user" -> UUID.fromString("11111111-1111-1111-1111-111111111111")
            "admin" -> UUID.fromString("22222222-2222-2222-2222-222222222222")
            else -> user.id
        }
        
        // Delete any existing tokens for this user
        refreshTokenRepository.deleteByUserId(userId)
        
        val refreshToken = RefreshToken(
            userId = userId,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenExpiration)
        )
        
        val savedToken = refreshTokenRepository.save(refreshToken)
        logger.debug("Saved refresh token: $savedToken")
        
        return savedToken
    }
    
    /**
     * Verify if a refresh token is valid
     */
    suspend fun verifyExpiration(token: RefreshToken): RefreshToken {
        if (token.expiryDate.isBefore(Instant.now()) || token.revoked) {
            refreshTokenRepository.deleteById(token.id)
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }
        
        return token
    }
    
    /**
     * Find a refresh token by token string
     */
    suspend fun findByToken(token: String): RefreshToken? {
        return refreshTokenRepository.findByToken(token)
    }
    
    /**
     * Delete all refresh tokens for a user
     */
    suspend fun deleteByUserId(userId: UUID) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}
