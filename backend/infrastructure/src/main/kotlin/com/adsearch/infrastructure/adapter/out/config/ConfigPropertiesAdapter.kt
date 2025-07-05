package com.adsearch.infrastructure.adapter.out.config

import com.adsearch.domain.port.out.ConfigPropertiesPort
import com.adsearch.infrastructure.service.ConfigService
import org.springframework.stereotype.Component

@Component
class ConfigPropertiesAdapter(private val configService: ConfigService) : ConfigPropertiesPort {
    override fun getPasswordResetTokenExpiration(): Long = configService.getPasswordResetTokenExpiration()
    override fun getRefreshTokenExpiration(): Long = configService.getRefreshTokenExpiration()
}
