package com.adsearch.infrastructure.adapter.`in`.rest.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

/**
 * DTO for password reset request
 */
data class PasswordResetRequestDto @JsonCreator constructor(
    @JsonProperty("username")
    @field:NotBlank(message = "Username is required")
    val username: String
)
