package com.adsearch.domain.port

import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse

/**
 * Port for authentication operations
 */
interface AuthenticationPort {
    /**
     * Authenticate a user with username and password
     */
    suspend fun authenticate(username: String, password: String): AuthResponse
}
