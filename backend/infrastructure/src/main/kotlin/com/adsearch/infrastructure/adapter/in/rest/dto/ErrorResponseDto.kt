package com.adsearch.infrastructure.adapter.`in`.rest.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

/**
 * Standard error response format
 */
data class ErrorResponseDto @JsonCreator constructor(
    @JsonProperty("timestamp") val timestamp: LocalDateTime = LocalDateTime.now(),

    @JsonProperty("status")
    @field:NotNull(message = "Status code cannot be null")
    @field:Min(value = 100, message = "Status code must be at least 100")
    val status: Int,

    @JsonProperty("error")
    @field:NotBlank(message = "Error cannot be blank")
    val error: String,

    @JsonProperty("message")
    @field:NotBlank(message = "Message cannot be blank")
    val message: String,

    @JsonProperty("path")
    @field:NotBlank(message = "Path cannot be blank")
    val path: String
)
