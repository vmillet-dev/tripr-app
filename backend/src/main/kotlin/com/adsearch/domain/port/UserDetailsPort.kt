package com.adsearch.domain.port

import com.adsearch.domain.model.User

/**
 * Port for retrieving user details
 */
interface UserDetailsPort {
    
    /**
     * Load user details by username
     */
    fun loadUserByUsername(username: String): User?
    
    /**
     * Load user details by user ID
     */
    suspend fun loadUserByUserId(userId: Long): User?
}
