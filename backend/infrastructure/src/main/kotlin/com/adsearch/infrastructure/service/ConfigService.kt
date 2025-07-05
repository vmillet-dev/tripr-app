package com.adsearch.infrastructure.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ConfigService(
    @param:Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long,
    @param:Value("\${password-reset.token-expiration}") private val passwordResetTokenExpiration: Long
) {
    fun getPasswordResetTokenExpiration(): Long = passwordResetTokenExpiration
    fun getRefreshTokenExpiration(): Long = refreshTokenExpiration
}

