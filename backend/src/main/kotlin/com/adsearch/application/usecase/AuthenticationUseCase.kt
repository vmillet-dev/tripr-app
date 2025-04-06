package com.adsearch.application.usecase

import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * Use case for authentication operations
 */
interface AuthenticationUseCase {

    /**
     * Authenticate a user with username and password
     */
    suspend fun authenticate(authRequest: AuthRequest): AuthResponse

    /**
     * Refresh an access token using a refresh token
     */
    suspend fun refreshToken(request: HttpServletRequest, response: HttpServletResponse): AuthResponse

    /**
     * Logout a user by invalidating their refresh tokens
     */
    suspend fun logout(request: HttpServletRequest, response: HttpServletResponse)

    /**
     * Get the current authenticated user
     */
    suspend fun getCurrentUser(request: HttpServletRequest): User?

    /**
     * Validate an access token
     */
    fun validateToken(token: String): Boolean

    /**
     * Register a new user
     */
    suspend fun register(authRequest: AuthRequest, email: String)
}
