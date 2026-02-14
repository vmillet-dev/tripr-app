package com.adsearch.domain.model

import com.adsearch.domain.model.enums.TokenTypeEnum
import java.time.Instant

/**
 * Domain model representing a password reset token
 */
data class PasswordResetTokenDom(
    override val userId: Long,
    override val token: String,
    override val expiryDate: Instant,
    override val type: TokenTypeEnum = TokenTypeEnum.PASSWORD_RESET,
    override val revoked: Boolean = false
) : TokenDom
