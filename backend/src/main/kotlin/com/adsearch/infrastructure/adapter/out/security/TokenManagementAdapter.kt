package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.TokenGenerationPort
import com.adsearch.domain.port.TokenManagementPort
import com.adsearch.domain.port.TokenValidationPort
import com.adsearch.domain.port.UserDetailsPort
import org.springframework.stereotype.Component

@Component
class TokenManagementAdapter(
    private val tokenValidationPort: TokenValidationPort,
    private val tokenGenerationPort: TokenGenerationPort,
    private val userDetailsPort: UserDetailsPort,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort
) : TokenManagementPort {
    
    override suspend fun refreshAccessToken(refreshToken: String): AuthResponse {
        val userId = tokenValidationPort.validateRefreshTokenAndGetUserId(refreshToken)
        val user = userDetailsPort.loadUserByUserId(userId) 
            ?: throw com.adsearch.common.exception.InvalidTokenException()
            
        val accessToken = tokenGenerationPort.generateAccessToken(user.id, user.username, user.roles)
        
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
