package com.adsearch.domain.model

import java.time.Instant

/**
 * Domain model representing a refresh token
 */
data class RefreshTokenDom(
    val userId: Long,
    val token: String,
    val expiryDate: Instant,
    val revoked: Boolean
) {
    fun isExpired(): Boolean = expiryDate.isBefore(Instant.now())
}
