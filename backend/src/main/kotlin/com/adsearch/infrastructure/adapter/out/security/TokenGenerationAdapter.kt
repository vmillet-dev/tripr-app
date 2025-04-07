package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.TokenGenerationPort
import com.adsearch.domain.service.TokenService
import com.adsearch.infrastructure.security.model.JwtUserDetails
import com.adsearch.infrastructure.security.service.JwtAccessTokenService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class TokenGenerationAdapter(
    private val jwtAccessTokenService: JwtAccessTokenService,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    @Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long
) : TokenGenerationPort {
    
    override fun generateAccessToken(userId: Long, username: String, roles: List<String>): String {
        val authorities = roles.map { SimpleGrantedAuthority(it) }
        val userDetails = JwtUserDetails(userId, username, "", authorities)
        return jwtAccessTokenService.generateAccessToken(userDetails)
    }
    
    override suspend fun createRefreshToken(userId: Long, username: String): RefreshToken {
        // Delete any existing tokens for this user
        refreshTokenPersistencePort.deleteByUserId(userId)
        
        // Create a new refresh token
        val refreshToken = RefreshToken(
            userId = userId,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenExpiration),
            revoked = false
        )
        
        return refreshTokenPersistencePort.save(refreshToken)
    }
}
