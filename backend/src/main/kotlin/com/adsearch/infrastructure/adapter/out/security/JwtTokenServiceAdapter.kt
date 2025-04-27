package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.port.api.JwtTokenServicePort
import com.adsearch.infrastructure.adapter.out.security.service.JwtTokenService
import org.springframework.stereotype.Component

@Component
class JwtTokenServiceAdapter(
    private val jwtTokenService: JwtTokenService
) : JwtTokenServicePort {

    override fun createAccessToken(userId: String, username: String, roles: List<String>): String =
        jwtTokenService.createAccessToken(userId, username, roles)

    override fun validateAccessTokenAndGetUsername(token: String): String? =
        jwtTokenService.validateAccessTokenAndGetUsername(token)
}
