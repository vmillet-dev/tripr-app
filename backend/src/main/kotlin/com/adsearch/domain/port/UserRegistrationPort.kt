package com.adsearch.domain.port

import com.adsearch.domain.model.AuthRequest

/**
 * Port for user registration operations
 */
interface UserRegistrationPort {
    
    /**
     * Register a new user
     */
    suspend fun register(authRequest: AuthRequest)
}
