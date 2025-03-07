package com.adsearch.infrastructure.web.dto

/**
 * DTO for authentication response
 */
data class AuthResponseDto(
    val accessToken: String,
    val username: String,
    val roles: List<String>
)
