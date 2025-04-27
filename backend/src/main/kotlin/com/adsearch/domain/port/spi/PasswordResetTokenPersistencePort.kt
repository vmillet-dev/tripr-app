package com.adsearch.domain.port.spi

import com.adsearch.domain.model.PasswordResetToken

/**
 * Port for password reset token repository operations
 */
interface PasswordResetTokenPersistencePort {

    /**
     * Find a token by token string
     */
    fun findByToken(token: String): PasswordResetToken?

    /**
     * Find all tokens for a user
     */
    fun findByUserId(userId: Long): List<PasswordResetToken>

    /**
     * Save a token
     */
    fun save(token: PasswordResetToken): PasswordResetToken

    /**
     * Delete a token by ID
     */
    fun deleteById(id: Long)

    /**
     * Delete all tokens for a user
     */
    fun deleteByUserId(userId: Long)
}
