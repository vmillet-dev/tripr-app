package com.adsearch.domain.model

import java.time.Instant

/**
 * Domain model representing a password reset token
 */
data class PasswordResetTokenDom(
    val userId: Long,
    val token: String,
    val expiryDate: Instant
) {
    fun isExpired(): Boolean = expiryDate.isBefore(Instant.now())
}
