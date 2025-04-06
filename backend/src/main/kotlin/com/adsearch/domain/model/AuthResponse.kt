package com.adsearch.domain.model

/**
 * Domain model representing an authentication response
 */
data class AuthResponse(
    val accessToken: String,
    val username: String,
    val roles: List<String>,
    val refreshToken: RefreshToken?,
)
