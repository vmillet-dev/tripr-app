package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.common.exception.functional.InvalidCredentialsException
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.AuthenticationPort
import com.adsearch.infrastructure.security.service.JwtUserDetailsService
import com.adsearch.infrastructure.security.service.JwtTokenService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthenticationAdapter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val jwtTokenService: JwtTokenService,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationPort {

    override fun authenticate(username: String, password: String): AuthResponse {
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

    override fun refreshAccessToken(refreshToken: String): AuthResponse {
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

    override fun generateHashedPassword(password: String): String {
       return passwordEncoder.encode(password)
    }

    override fun generatePasswordToken(userId: Long): PasswordResetToken {
        return jwtTokenService.generatePasswordResetToken(userId)
    }
}
