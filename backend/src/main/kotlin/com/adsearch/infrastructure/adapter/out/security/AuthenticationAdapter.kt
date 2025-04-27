package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.api.AuthenticationPort
import com.adsearch.infrastructure.adapter.out.security.service.AuthService
import org.springframework.stereotype.Component

@Component
class AuthenticationAdapter(
    private val authService: AuthService,
) : AuthenticationPort {

    override fun authenticate(username: String, password: String) = authService.authenticate(username, password)

    override fun loadAuthenticateUserByUsername(username: String): UserDom = authService.loadAuthenticateUserByUsername(username)

    override fun generateHashedPassword(password: String): String = authService.generateHashedPassword(password)

    override fun generatePasswordResetToken(userId: Long): PasswordResetTokenDom = authService.generatePasswordResetToken(userId)

    override fun generateRefreshToken(userId: Long): RefreshTokenDom = authService.generateRefreshToken(userId)
}
