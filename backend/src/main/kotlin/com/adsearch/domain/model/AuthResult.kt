package com.adsearch.domain.model

/**
 * Sealed class representing authentication operation results
 */
sealed class AuthResult {
    /**
     * Successful authentication
     */
    data class Success(
        val response: AuthResponse
    ) : AuthResult()

    /**
     * Invalid credentials
     */
    data class InvalidCredentials(
        val message: String = "Invalid username or password"
    ) : AuthResult()

    /**
     * Invalid or expired token
     */
    data class InvalidToken(
        val message: String
    ) : AuthResult()

    /**
     * User not found
     */
    data class UserNotFound(
        val username: String
    ) : AuthResult()
}
