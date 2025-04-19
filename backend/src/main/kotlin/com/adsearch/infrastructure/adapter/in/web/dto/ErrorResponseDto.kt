package com.adsearch.infrastructure.adapter.`in`.web.dto

import java.time.LocalDateTime

/**
 * Standard error response format
 */
data class ErrorResponseDto(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
