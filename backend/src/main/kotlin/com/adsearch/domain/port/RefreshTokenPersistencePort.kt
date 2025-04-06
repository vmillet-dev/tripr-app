package com.adsearch.domain.port

import com.adsearch.domain.model.RefreshToken

/**
 * Port for refresh token repository operations
 */
interface RefreshTokenPersistencePort {

    /**
     * Find a refresh token by token string
     */
    suspend fun findByToken(token: String): RefreshToken?

    /**
     * Find all refresh tokens for a user
     */
    suspend fun findByUserId(userId: Long): List<RefreshToken>

    /**
     * Save a refresh token
     */
    suspend fun save(refreshToken: RefreshToken): RefreshToken

    /**
     * Delete a refresh token
     */
    suspend fun deleteById(id: Long)

    /**
     * Delete all refresh tokens for a user
     */
    suspend fun deleteByUserId(userId: Long)
}
