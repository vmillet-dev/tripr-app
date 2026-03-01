package com.adsearch.domain.model

import com.adsearch.domain.enums.TokenTypeEnum
import java.time.Instant

/**
 * Domain model representing a refresh token
 */
data class RefreshToken(
    override val userId: Long,
    override val token: String,
    override val expiryDate: Instant,
    override val revoked: Boolean,
    override val type: TokenTypeEnum = TokenTypeEnum.REFRESH
) : Token
