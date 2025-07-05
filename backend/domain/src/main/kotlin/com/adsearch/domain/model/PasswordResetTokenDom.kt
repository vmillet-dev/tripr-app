package com.adsearch.domain.model

import java.time.Instant

/**
 * Domain model representing a password reset token
 */
data class PasswordResetTokenDom(
    val user: UserDom,
    val token: String,
    val expiryDate: Instant,
    val used: Boolean
) {
    fun isExpired(): Boolean = expiryDate.isBefore(Instant.now())
}
