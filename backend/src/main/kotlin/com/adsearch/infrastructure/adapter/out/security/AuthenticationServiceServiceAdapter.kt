package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.api.AuthenticationServicePort
import com.adsearch.infrastructure.adapter.out.security.service.AuthenticationService
import org.springframework.stereotype.Component

@Component
class AuthenticationServiceServiceAdapter(
    private val authenticationService: AuthenticationService,
) : AuthenticationServicePort {

    override fun authenticate(username: String, password: String) =
        authenticationService.authenticate(username, password)

    override fun loadAuthenticateUserByUsername(username: String): UserDom =
        authenticationService.loadAuthenticateUserByUsername(username)

    override fun generateHashedPassword(password: String): String =
        authenticationService.generateHashedPassword(password)

    override fun generatePasswordResetToken(userId: Long): PasswordResetTokenDom =
        authenticationService.generatePasswordResetToken(userId)

    override fun generateRefreshToken(userId: Long): RefreshTokenDom =
        authenticationService.generateRefreshToken(userId)
}
