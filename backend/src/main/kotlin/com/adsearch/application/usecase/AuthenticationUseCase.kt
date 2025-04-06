package com.adsearch.application.usecase

import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse

/**
 * Use case for authentication operations
 */
interface AuthenticationUseCase {

    /**
     * Authenticate a user with username and password
     */
    suspend fun login(authRequest: AuthRequest): AuthResponse

    /**
     * Register a new user
     */
    suspend fun register(authRequest: AuthRequest, email: String)

    /**
     * Refresh an access token using a refresh token
     */
    suspend fun refreshAccessToken(refreshToken: String?): AuthResponse

    /**
     * Logout a user by invalidating their refresh tokens
     */
    suspend fun logout(refreshToken: String?)
}
