package com.adsearch.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Domain model representing a refresh token
 */
data class RefreshToken(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val token: String,
    val expiryDate: Instant,
    val revoked: Boolean = false
)
