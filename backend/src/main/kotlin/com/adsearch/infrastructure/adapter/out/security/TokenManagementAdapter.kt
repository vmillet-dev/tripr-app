package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.TokenManagementPort
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.security.service.JwtAccessTokenService
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TokenManagementAdapter(
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val userPersistencePort: UserPersistencePort,
    private val jwtAccessTokenService: JwtAccessTokenService
) : TokenManagementPort {
    
    override suspend fun refreshAccessToken(refreshToken: String): AuthResponse {
        val storedToken: RefreshToken? = refreshTokenPersistencePort.findByToken(refreshToken)
        
        if (storedToken == null) {
            throw InvalidTokenException()
        }
        
        // Verify token expiration directly here
        if (storedToken.expiryDate.isBefore(Instant.now()) || storedToken.revoked) {
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }
        
        val userId = storedToken.userId
        
        val user = userPersistencePort.findById(userId) 
            ?: throw InvalidTokenException()
            
        val userDetails = com.adsearch.infrastructure.security.model.JwtUserDetails(
            id = user.id,
            username = user.username,
            hash = user.password,
            authorities = user.roles.map { org.springframework.security.core.authority.SimpleGrantedAuthority(it) }
        )
        
        val accessToken = jwtAccessTokenService.generateAccessToken(userDetails)
        
        return AuthResponse(
            accessToken = accessToken,
            username = user.username,
            roles = user.roles,
            refreshToken = null
        )
    }
    
    override suspend fun logout(refreshToken: String) {
        val storedToken = refreshTokenPersistencePort.findByToken(refreshToken)
        
        if (storedToken != null) {
            refreshTokenPersistencePort.deleteByUserId(storedToken.userId)
        }
    }
}
