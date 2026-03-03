package com.adsearch.domain.model

import com.adsearch.domain.enums.TokenTypeEnum
import java.time.Instant

/**
 * Interface representing a common token domain model
 */
interface Token {
    val userId: Long
    val token: String
    val expiryDate: Instant
    val type: TokenTypeEnum
    val revoked: Boolean

    fun isExpired(): Boolean = expiryDate.isBefore(Instant.now())
}
