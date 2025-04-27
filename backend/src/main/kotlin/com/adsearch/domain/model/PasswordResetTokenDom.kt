package com.adsearch.domain.model

import java.time.Instant

/**
 * Domain model representing a password reset token
 */
data class PasswordResetTokenDom(
    val id: Long = 0,
    val userId: Long,
    val token: String,
    val expiryDate: Instant,
    val used: Boolean = false
) {
    fun isExpired(): Boolean = expiryDate.isBefore(Instant.now())
}
