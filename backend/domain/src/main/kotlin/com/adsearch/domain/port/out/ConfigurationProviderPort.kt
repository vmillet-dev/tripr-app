package com.adsearch.domain.port.out

interface ConfigurationProviderPort {
    fun getPasswordResetTokenExpiration(): Long
    fun getRefreshTokenExpiration(): Long
}
