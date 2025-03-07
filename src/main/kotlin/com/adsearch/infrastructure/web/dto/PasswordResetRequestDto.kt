package com.adsearch.infrastructure.web.dto

import jakarta.validation.constraints.NotBlank

/**
 * DTO for password reset request
 */
data class PasswordResetRequestDto(
    @field:NotBlank(message = "Username is required")
    val username: String
)
