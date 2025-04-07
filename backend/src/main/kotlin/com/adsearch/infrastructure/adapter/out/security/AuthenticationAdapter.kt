package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.common.exception.InvalidCredentialsException
import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.AuthenticationPort
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.adapter.out.security.mapper.JwtUserDetailsMapper
import com.adsearch.infrastructure.security.model.JwtUserDetails
import com.adsearch.infrastructure.security.service.JwtAccessTokenService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class AuthenticationAdapter(
    private val authenticationManager: AuthenticationManager,
    private val jwtAccessTokenService: JwtAccessTokenService,
    private val passwordEncoder: PasswordEncoder,
    private val userPersistencePort: UserPersistencePort,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val jwtUserDetailsMapper: JwtUserDetailsMapper,
    @Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long
) : AuthenticationPort {
    
    override suspend fun authenticate(username: String, password: String): AuthResponse {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (ex: BadCredentialsException) {
            throw InvalidCredentialsException()
        }
        
        val user = loadUserByUsername(username) 
            ?: throw InvalidCredentialsException()
            
        val refreshToken = createRefreshToken(user.id, user.username)
        val accessToken = generateAccessToken(user.id, user.username, user.roles)
        
        return AuthResponse(
            accessToken = accessToken,
            username = user.username,
            roles = user.roles,
            refreshToken = refreshToken
        )
    }
    
    override suspend fun register(authRequest: AuthRequest) {
        val user = User(
            username = authRequest.username,
            password = passwordEncoder.encode(authRequest.password),
            roles = mutableListOf("USER")
        )
        
        userPersistencePort.save(user)
    }
    
    override suspend fun refreshAccessToken(refreshToken: String): AuthResponse {
        val userId = validateRefreshTokenAndGetUserId(refreshToken)
        val user = loadUserByUserId(userId) ?: throw InvalidTokenException()
        
        val accessToken = generateAccessToken(user.id, user.username, user.roles)
        
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
    
    override fun validateTokenAndGetUsername(token: String): String? {
        return jwtAccessTokenService.validateTokenAndGetUsername(token)
    }
    
    override fun loadUserByUsername(username: String): User? {
        return userPersistencePort.findByUsername(username)
    }
    
    override suspend fun loadUserByUserId(userId: Long): User? {
        return userPersistencePort.findById(userId)
    }
    
    override fun generateAccessToken(userId: Long, username: String, roles: List<String>): String {
        val user = User(id = userId, username = username, password = "", roles = roles.toMutableList())
        val userDetails = jwtUserDetailsMapper.toJwtUserDetails(user)
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
