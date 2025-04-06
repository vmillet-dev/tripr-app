package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.TokenValidationPort
import com.adsearch.domain.service.TokenService
import com.adsearch.infrastructure.security.service.JwtAccessTokenService
import org.springframework.stereotype.Component

@Component
class TokenValidationAdapter(
    private val jwtAccessTokenService: JwtAccessTokenService,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val tokenService: TokenService
) : TokenValidationPort {
    
    override fun validateTokenAndGetUsername(token: String): String? {
        return jwtAccessTokenService.validateTokenAndGetUsername(token)
    }
    
    override suspend fun validateRefreshTokenAndGetUserId(token: String): Long {
        val refreshToken: RefreshToken? = refreshTokenPersistencePort.findByToken(token)
        
        if (refreshToken == null) {
            throw com.adsearch.common.exception.InvalidTokenException()
        }
        
        tokenService.verifyExpiration(refreshToken)
        return refreshToken.userId
    }
}
