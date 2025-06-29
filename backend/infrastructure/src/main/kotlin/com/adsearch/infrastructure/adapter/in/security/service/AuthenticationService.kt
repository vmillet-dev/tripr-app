package com.adsearch.infrastructure.adapter.`in`.security.service

import com.adsearch.domain.model.UserDom
import com.adsearch.infrastructure.adapter.`in`.security.model.JwtUserDetails
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val authenticationManager: AuthenticationManager,
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long,
    @Value("\${password-reset.token-expiration}") private val passwordResetTokenExpiration: Long,
) {
    fun authenticate(username: String, password: String) {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
    }

    fun loadAuthenticateUserByUsername(username: String): UserDom {
        val userDetails: JwtUserDetails = jwtUserDetailsService.loadUserByUsername(username)

        return UserDom(
            id = userDetails.id,
            username = userDetails.username,
            email = "",
            roles = userDetails.authorities.map { it.authority },
            password = ""
        )
    }

    fun generateHashedPassword(password: String): String = passwordEncoder.encode(password)
    fun getPasswordResetTokenExpiration(): Long = passwordResetTokenExpiration
    fun getRefreshTokenExpiration(): Long = refreshTokenExpiration
}
