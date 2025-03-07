package com.adsearch.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Domain model representing a password reset token
 */
data class PasswordResetToken(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val token: String,
    val expiryDate: Instant,
    val used: Boolean = false
)
