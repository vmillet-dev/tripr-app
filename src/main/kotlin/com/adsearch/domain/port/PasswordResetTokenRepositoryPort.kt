package com.adsearch.domain.port

import com.adsearch.domain.model.PasswordResetToken
import java.util.UUID

/**
 * Port for password reset token repository operations
 */
interface PasswordResetTokenRepositoryPort {
    
    /**
     * Find a token by token string
     */
    suspend fun findByToken(token: String): PasswordResetToken?
    
    /**
     * Find all tokens for a user
     */
    suspend fun findByUserId(userId: UUID): List<PasswordResetToken>
    
    /**
     * Save a token
     */
    suspend fun save(token: PasswordResetToken): PasswordResetToken
    
    /**
     * Delete a token by ID
     */
    suspend fun deleteById(id: UUID)
    
    /**
     * Delete all tokens for a user
     */
    suspend fun deleteByUserId(userId: UUID)
}
