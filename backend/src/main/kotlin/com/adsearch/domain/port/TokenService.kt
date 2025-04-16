package com.adsearch.domain.port

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.RefreshToken
import java.time.Instant

/**
 * Service for token operations
 */
interface TokenService {
    /**
     * Generate an access token for a user
     */
    fun generateAccessToken(userId: Long, username: String, roles: List<String>): String
    
    /**
     * Generate a refresh token for a user
     */
    fun generateRefreshToken(userId: Long): RefreshToken
    
    /**
     * Generate a password reset token for a user
     */
    fun generatePasswordResetToken(userId: Long): PasswordResetToken
    
    /**
     * Validate an access token
     */
    fun validateAccessToken(token: String): String?
    
    /**
     * Validate a refresh token
     */
    fun validateRefreshToken(token: String): Long
    
    /**
     * Check if a token is expired
     */
    fun isTokenExpired(expiryDate: Instant): Boolean
}
