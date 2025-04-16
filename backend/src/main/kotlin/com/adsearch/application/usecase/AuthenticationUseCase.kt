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
    fun login(username: String, password: String): AuthResponse

    /**
     * Register a new user
     */
    fun register(authRequest: AuthRequest, email: String)

    /**
     * Refresh an access token using a refresh token
     */
    fun refreshAccessToken(refreshToken: String?): AuthResponse

    /**
     * Logout a user by invalidating their refresh tokens
     */
    fun logout(refreshToken: String?)

    /**
     * Request a password reset for a user
     */
    fun requestPasswordReset(username: String)

    /**
     * Reset a user's password using a token
     */
    fun resetPassword(token: String, newPassword: String)

    /**
     * Validate a password reset token
     */
    fun validateToken(token: String): Boolean}
