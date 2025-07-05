package com.adsearch.domain.port.out

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
     * Save a refresh token
     */
    fun save(refreshTokenDom: RefreshTokenDom)

    /**
     * Delete a refresh token
     */
    fun deleteByToken(token: String)

    /**
     * Delete all refresh tokens for a user
     */
    fun deleteByUserId(userId: Long)
}
