package com.adsearch.domain.model

/**
 * Domain model representing an authentication response
 */
data class AuthResponseDom(
    val user: UserDom,
    val accessToken: String,
    val refreshToken: String? = null
)
