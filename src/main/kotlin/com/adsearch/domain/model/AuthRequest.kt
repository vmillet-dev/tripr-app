package com.adsearch.domain.model

/**
 * Domain model representing an authentication request
 */
data class AuthRequest(
    val username: String,
    val password: String
)
