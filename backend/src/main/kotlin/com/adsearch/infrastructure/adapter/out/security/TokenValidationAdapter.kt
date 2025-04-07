package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.TokenValidationPort
import com.adsearch.infrastructure.security.service.JwtAccessTokenService
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TokenValidationAdapter(
    private val jwtAccessTokenService: JwtAccessTokenService,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort
) : TokenValidationPort {
    
    override fun validateTokenAndGetUsername(token: String): String? {
        return jwtAccessTokenService.validateTokenAndGetUsername(token)
    }
    
    override suspend fun validateRefreshTokenAndGetUserId(token: String): Long {
        val refreshToken: RefreshToken? = refreshTokenPersistencePort.findByToken(token)
        
        if (refreshToken == null) {
            throw InvalidTokenException()
        }
        
        // Verify token expiration directly here
        if (refreshToken.expiryDate.isBefore(Instant.now()) || refreshToken.revoked) {
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }
        
        return refreshToken.userId
    }
}
