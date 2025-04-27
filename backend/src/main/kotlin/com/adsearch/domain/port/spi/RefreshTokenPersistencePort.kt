package com.adsearch.domain.port.spi

import com.adsearch.domain.model.RefreshTokenDom

/**
 * Port for refresh token repository operations
 */
interface RefreshTokenPersistencePort {

    /**
     * Find a refresh token by token string
     */
    fun findByToken(token: String): RefreshTokenDom?

    /**
     * Find all refresh tokens for a user
     */
    fun findByUserId(userId: Long): List<RefreshTokenDom>

    /**
     * Save a refresh token
     */
    fun save(refreshTokenDom: RefreshTokenDom): RefreshTokenDom

    /**
     * Delete a refresh token
     */
    fun deleteById(id: Long)

    /**
     * Delete all refresh tokens for a user
     */
    fun deleteByUserId(userId: Long)
}
