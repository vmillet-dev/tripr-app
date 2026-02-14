package com.adsearch.infrastructure.adapter.out

import com.adsearch.domain.port.out.ConfigurationProviderPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ConfigurationProviderAdapter(
    @param:Value($$"${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long,
    @param:Value($$"${password-reset.token-expiration}") private val passwordResetTokenExpiration: Long
) : ConfigurationProviderPort {
    override fun getPasswordResetTokenExpiration(): Long = refreshTokenExpiration
    override fun getRefreshTokenExpiration(): Long = passwordResetTokenExpiration
}
