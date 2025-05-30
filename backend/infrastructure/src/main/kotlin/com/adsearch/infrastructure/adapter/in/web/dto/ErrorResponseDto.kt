package com.adsearch.infrastructure.adapter.`in`.web.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

/**
 * Standard error response format
 */
data class ErrorResponseDto(
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @field:NotNull(message = "Status code cannot be null")
    @field:Min(value = 100, message = "Status code must be at least 100")
    val status: Int,

    @field:NotBlank(message = "Error cannot be blank")
    val error: String,

    @field:NotBlank(message = "Message cannot be blank")
    val message: String,

    @field:NotBlank(message = "Path cannot be blank")
    val path: String
)
