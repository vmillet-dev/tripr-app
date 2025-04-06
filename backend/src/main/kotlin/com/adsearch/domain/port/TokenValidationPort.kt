package com.adsearch.domain.port

/**
 * Port for token validation operations
 */
interface TokenValidationPort {
    
    /**
     * Validate a token and return the associated username
     */
    fun validateTokenAndGetUsername(token: String): String?
    
    /**
     * Validate a refresh token and return the associated user ID
     */
    suspend fun validateRefreshTokenAndGetUserId(token: String): Long
}
