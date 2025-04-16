package com.adsearch.infrastructure.security.port

import com.adsearch.domain.model.RefreshToken
import com.adsearch.infrastructure.security.model.JwtUserDetails

/**
 * Port for JWT token operations
 */
interface JwtTokenServicePort {
    /**
     * Generate a JWT token for a user
     */
    fun createAccessToken(user: JwtUserDetails): String
    
    /**
     * Create a refresh token for a user
     */
    suspend fun createRefreshToken(user: JwtUserDetails): RefreshToken
    
    /**
     * Validate a JWT token
     */
    fun validateAccessTokenAndGetUsername(token: String): String?
    
    /**
     * Validate a refresh token and get the associated username
     */
    suspend fun validateRefreshTokenAndGetUsername(givenToken: String): String
}
