package com.adsearch.infrastructure.adapter.`in`.rest.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * DTO for password reset
 */
data class PasswordResetDto @JsonCreator constructor(
    @JsonProperty("token")
    @field:NotBlank(message = "Token is required")
    val token: String,

    @JsonProperty("newPassword")
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val newPassword: String
)
