package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.common.exception.InvalidCredentialsException
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.AuthenticationPort
import com.adsearch.infrastructure.security.port.JwtUserDetailsServicePort
import com.adsearch.infrastructure.security.port.JwtTokenServicePort
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class AuthenticationAdapter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUserDetailsService: JwtUserDetailsServicePort,
    private val jwtTokenService: JwtTokenServicePort,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${password-reset.token-expiration}") private val tokenExpiration: Long,
) : AuthenticationPort {

    override suspend fun authenticate(username: String, password: String): AuthResponse {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (ex: BadCredentialsException) {
            throw InvalidCredentialsException()
        }

        val userDetails = jwtUserDetailsService.loadUserByUsername(username)
        val refreshToken: RefreshToken = jwtTokenService.createRefreshToken(userDetails)
        val accessToken: String = jwtTokenService.createAccessToken(userDetails)

        return AuthResponse(
            accessToken = accessToken,
            username = userDetails.username,
            roles = userDetails.authorities.map { it.authority },
            refreshToken = refreshToken
        )
    }

    override suspend fun refreshAccessToken(refreshToken: String): AuthResponse {

        val username = jwtTokenService.validateRefreshTokenAndGetUsername(refreshToken)
        val userDetails = jwtUserDetailsService.loadUserByUsername(username)
        val accessToken = jwtTokenService.createAccessToken(userDetails)

        return AuthResponse(
            accessToken = accessToken,
            username = userDetails.username,
            roles = userDetails.authorities.map { it.authority },
            refreshToken = null
        )
    }

    override suspend fun generateHashedPassword(password: String): String {
       return passwordEncoder.encode(password)
    }

    override suspend fun generatePasswordToken(userId: Long): PasswordResetToken {
        return PasswordResetToken(
            userId = userId,
            token = java.util.UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(tokenExpiration)
        )
    }
}
