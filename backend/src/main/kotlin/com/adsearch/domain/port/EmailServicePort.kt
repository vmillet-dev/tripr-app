package com.adsearch.domain.port

/**
 * Port for email service operations
 */
interface EmailServicePort {

    /**
     * Send a password reset email
     */
    fun sendPasswordResetEmail(to: String, token: String)
}
