package com.adsearch.domain.port.spi

import com.adsearch.domain.model.PasswordResetTokenDom

/**
 * Port for password reset token repository operations
 */
interface PasswordResetTokenPersistencePort {

    /**
     * Find a token by token string
     */
    fun findByToken(token: String): PasswordResetTokenDom?

    /**
     * Find all tokens for a user
     */
    fun findByUserId(userId: Long): List<PasswordResetTokenDom>

    /**
     * Save a token
     */
    fun save(token: PasswordResetTokenDom): PasswordResetTokenDom

    /**
     * Delete a token by ID
     */
    fun deleteById(id: Long)

    /**
     * Delete all tokens for a user
     */
    fun deleteByUserId(userId: Long)
}
