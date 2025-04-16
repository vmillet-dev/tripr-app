package com.adsearch.infrastructure.security.port

import com.adsearch.infrastructure.security.model.JwtUserDetails

/**
 * Port for loading user details for JWT authentication
 */
interface JwtUserDetailsServicePort {
    /**
     * Load user details by username
     */
    fun loadUserByUsername(username: String): JwtUserDetails
}
