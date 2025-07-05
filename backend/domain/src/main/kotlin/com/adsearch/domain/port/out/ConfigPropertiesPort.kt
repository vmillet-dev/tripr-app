package com.adsearch.domain.port.out

interface ConfigPropertiesPort {
    fun getPasswordResetTokenExpiration(): Long
    fun getRefreshTokenExpiration(): Long
}
