package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.common.exception.InvalidCredentialsException
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.AuthenticationPort
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.security.service.JwtUserDetailsService
import com.adsearch.infrastructure.security.service.JwtAccessTokenService
import com.adsearch.infrastructure.security.service.JwtRefreshTokenService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthenticationAdapter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val jwtRefreshTokenService: JwtRefreshTokenService,
    private val jwtAccessTokenService: JwtAccessTokenService,
    private val passwordEncoder: PasswordEncoder,
    private val userPersistencePort: UserPersistencePort,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort
) : AuthenticationPort {

    override suspend fun authenticate(username: String, password: String): AuthResponse {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (ex: BadCredentialsException) {
            throw InvalidCredentialsException()
        }

        val userDetails = jwtUserDetailsService.loadUserByUsername(username)
        val refreshToken: RefreshToken = jwtRefreshTokenService.createRefreshToken(userDetails)
        val accessToken: String = jwtAccessTokenService.generateAccessToken(userDetails)

        return AuthResponse(
            accessToken = accessToken,
            username = userDetails.username,
            roles = userDetails.authorities.map { it.authority },
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

        val userId = jwtRefreshTokenService.validateRefreshTokenAndGetUserId(refreshToken)
        val userDetails = jwtUserDetailsService.loadUserByUserId(userId)
        val accessToken = jwtAccessTokenService.generateAccessToken(userDetails)

        return AuthResponse(
            accessToken = accessToken,
            username = userDetails.username,
            roles = userDetails.authorities.map { it.authority },
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
