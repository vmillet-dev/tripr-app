package com.adsearch.domain.port

import com.adsearch.domain.model.AuthResponse

/**
 * Port for token management operations
 */
interface TokenManagementPort {
    
    /**
     * Refresh an access token using a refresh token
     */
    suspend fun refreshAccessToken(refreshToken: String): AuthResponse
    
    /**
     * Logout a user by invalidating their refresh tokens
     */
    suspend fun logout(refreshToken: String)
}
