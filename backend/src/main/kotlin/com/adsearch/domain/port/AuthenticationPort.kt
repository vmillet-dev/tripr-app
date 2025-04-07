package com.adsearch.domain.port

import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User

/**
 * Port for authentication operations
 */
interface AuthenticationPort {
    /**
     * Authenticate a user with username and password
     */
    suspend fun authenticate(username: String, password: String): AuthResponse
    
    /**
     * Register a new user
     */
    suspend fun register(authRequest: AuthRequest)
    
    /**
     * Refresh an access token using a refresh token
     */
    suspend fun refreshAccessToken(refreshToken: String): AuthResponse
    
    /**
     * Logout a user by invalidating their refresh tokens
     */
    suspend fun logout(refreshToken: String)
    
    /**
     * Validate a token and return the associated username
     */
    fun validateTokenAndGetUsername(token: String): String?
    
    /**
     * Load user details by username
     */
    fun loadUserByUsername(username: String): User?
    
    /**
     * Load user details by user ID
     */
    suspend fun loadUserByUserId(userId: Long): User?
    
    /**
     * Generate an access token for a user
     */
    fun generateAccessToken(userId: Long, username: String, roles: List<String>): String
    
    /**
     * Create a refresh token for a user
     */
    suspend fun createRefreshToken(userId: Long, username: String): RefreshToken
    
    /**
     * Validate a refresh token and return the associated user ID
     */
    suspend fun validateRefreshTokenAndGetUserId(token: String): Long
}
