package com.adsearch.domain.auth

/**
 * An authentication response
 */
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String? = null
)
