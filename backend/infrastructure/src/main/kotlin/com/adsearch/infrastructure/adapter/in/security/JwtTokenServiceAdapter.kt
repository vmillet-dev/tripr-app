package com.adsearch.infrastructure.adapter.`in`.security

import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.JwtTokenServicePort
import com.adsearch.infrastructure.service.JwtTokenService
import org.springframework.stereotype.Component

@Component
class JwtTokenServiceAdapter(
    private val jwtTokenService: JwtTokenService
) : JwtTokenServicePort {

    override fun createAccessToken(user: UserDom): String {
        return jwtTokenService.createAccessToken(user)
    }

    override fun validateAccessTokenAndGetUsername(token: String): String? {
        return jwtTokenService.validateAccessTokenAndGetUsername(token)
    }
}
