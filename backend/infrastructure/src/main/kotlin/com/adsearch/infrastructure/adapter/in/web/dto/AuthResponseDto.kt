package com.adsearch.infrastructure.adapter.`in`.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

/**
 * DTO for authentication response
 */
data class AuthResponseDto(
    @field:NotBlank(message = "Access token is required")
    val accessToken: String,
    @field:NotBlank(message = "Username is required")
    val username: String,
    @field:NotEmpty(message = "Roles must not be empty")
    val roles: List<String>
)
