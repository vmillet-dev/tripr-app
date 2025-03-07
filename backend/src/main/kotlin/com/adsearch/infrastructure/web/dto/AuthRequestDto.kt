package com.adsearch.infrastructure.web.dto

import jakarta.validation.constraints.NotBlank

/**
 * DTO for authentication request
 */
data class AuthRequestDto(
    @field:NotBlank(message = "Username is required")
    val username: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String
)
