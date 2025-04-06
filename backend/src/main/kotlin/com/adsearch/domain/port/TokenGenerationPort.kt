package com.adsearch.domain.port

import com.adsearch.domain.model.RefreshToken

/**
 * Port for token generation operations
 */
interface TokenGenerationPort {
    
    /**
     * Generate an access token for a user
     */
    fun generateAccessToken(userId: Long, username: String, roles: List<String>): String
    
    /**
     * Create a refresh token for a user
     */
    suspend fun createRefreshToken(userId: Long, username: String): RefreshToken
}
