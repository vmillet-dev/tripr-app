package com.adsearch.domain.model.auth

/**
 * An authentication response
 */
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String? = null
)
