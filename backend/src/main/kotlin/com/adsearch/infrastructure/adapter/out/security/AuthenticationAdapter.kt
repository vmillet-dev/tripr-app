package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.common.exception.InvalidCredentialsException
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.port.AuthenticationPort
import com.adsearch.domain.port.TokenGenerationPort
import com.adsearch.domain.port.UserDetailsPort
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component

@Component
class AuthenticationAdapter(
    private val authenticationManager: AuthenticationManager,
    private val tokenGenerationPort: TokenGenerationPort,
    private val userDetailsPort: UserDetailsPort
) : AuthenticationPort {
    
    override suspend fun authenticate(username: String, password: String): AuthResponse {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (ex: BadCredentialsException) {
            throw InvalidCredentialsException()
        }
        
        val user = userDetailsPort.loadUserByUsername(username) 
            ?: throw InvalidCredentialsException()
            
        val refreshToken = tokenGenerationPort.createRefreshToken(user.id, user.username)
        val accessToken = tokenGenerationPort.generateAccessToken(user.id, user.username, user.roles)
        
        return AuthResponse(
            accessToken = accessToken,
            username = user.username,
            roles = user.roles,
            refreshToken = refreshToken
        )
    }
}
