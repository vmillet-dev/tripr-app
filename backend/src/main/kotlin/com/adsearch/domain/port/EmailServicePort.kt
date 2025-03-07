package com.adsearch.domain.port

/**
 * Port for email service operations
 */
interface EmailServicePort {
    
    /**
     * Send a password reset email
     */
    suspend fun sendPasswordResetEmail(to: String, resetLink: String)
}
