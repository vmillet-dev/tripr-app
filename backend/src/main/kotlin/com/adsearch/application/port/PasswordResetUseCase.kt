package com.adsearch.application.port

/**
 * Use case for password reset operations
 */
interface PasswordResetUseCase {
    
    /**
     * Request a password reset for a user
     */
    suspend fun requestPasswordReset(username: String)
    
    /**
     * Reset a user's password using a token
     */
    suspend fun resetPassword(token: String, newPassword: String)
    
    /**
     * Validate a password reset token
     */
    suspend fun validateToken(token: String): Boolean
}
