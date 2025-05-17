package com.adsearch.infrastructure.adapter.`in`.security

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.api.AuthenticationServicePort
import com.adsearch.infrastructure.adapter.`in`.security.impl.AuthenticationService
import org.springframework.stereotype.Component

@Component
class AuthenticationServiceAdapter(
    private val authenticationService: AuthenticationService,
) : AuthenticationServicePort {

    override fun authenticate(username: String, password: String) {
        return authenticationService.authenticate(username, password)
    }

    override fun loadAuthenticateUserByUsername(username: String): UserDom {
        return authenticationService.loadAuthenticateUserByUsername(username)
    }

    override fun generateHashedPassword(password: String): String {
        return authenticationService.generateHashedPassword(password)
    }

    override fun generatePasswordResetToken(userId: Long): PasswordResetTokenDom {
        return authenticationService.generatePasswordResetToken(userId)
    }

    override fun generateRefreshToken(userId: Long): RefreshTokenDom {
        return authenticationService.generateRefreshToken(userId)
    }
}
