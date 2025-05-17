package com.adsearch.infrastructure.adapter.`in`.security

import com.adsearch.domain.port.`in`.JwtTokenServicePort
import com.adsearch.infrastructure.adapter.`in`.security.impl.JwtTokenService
import org.springframework.stereotype.Component

@Component
class JwtTokenServiceAdapter(
    private val jwtTokenService: JwtTokenService
) : JwtTokenServicePort {

    override fun createAccessToken(userId: String, username: String, roles: List<String>): String {
        return jwtTokenService.createAccessToken(userId, username, roles)
    }

    override fun validateAccessTokenAndGetUsername(token: String): String? {
        return jwtTokenService.validateAccessTokenAndGetUsername(token)
    }
}
