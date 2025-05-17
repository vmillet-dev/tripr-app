package com.adsearch.domain.model

import java.time.Instant

/**
 * Domain model representing a refresh token
 */
data class RefreshTokenDom(
    val id: Long = 0,
    val userId: Long,
    val token: String,
    val expiryDate: Instant,
    val revoked: Boolean = false
) {
    fun isExpired(): Boolean = expiryDate.isBefore(Instant.now())
}
