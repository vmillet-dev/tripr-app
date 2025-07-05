package com.adsearch.infrastructure.adapter.`in`.web.dto

/**
 * DTO for authentication response
 */
data class AuthResponseDto(
    val accessToken: String,
    val username: String,
    val roles: Set<String>
)
