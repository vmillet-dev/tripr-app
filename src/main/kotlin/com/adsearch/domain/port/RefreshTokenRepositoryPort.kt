package com.adsearch.domain.port

import com.adsearch.domain.model.RefreshToken
import java.util.UUID

/**
 * Port for refresh token repository operations
 */
interface RefreshTokenRepositoryPort {
    
    /**
     * Find a refresh token by token string
     */
    suspend fun findByToken(token: String): RefreshToken?
    
    /**
     * Find all refresh tokens for a user
     */
    suspend fun findByUserId(userId: UUID): List<RefreshToken>
    
    /**
     * Save a refresh token
     */
    suspend fun save(refreshToken: RefreshToken): RefreshToken
    
    /**
     * Delete a refresh token
     */
    suspend fun deleteById(id: UUID)
    
    /**
     * Delete all refresh tokens for a user
     */
    suspend fun deleteByUserId(userId: UUID)
}
