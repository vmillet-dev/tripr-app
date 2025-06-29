package com.adsearch.infrastructure.adapter.`in`.security

import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.AuthenticationServicePort
import com.adsearch.infrastructure.adapter.`in`.security.service.AuthenticationService
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

    override fun getPasswordResetTokenExpiration(): Long {
        return authenticationService.getPasswordResetTokenExpiration()
    }

    override fun getRefreshTokenExpiration(): Long {
        return authenticationService.getRefreshTokenExpiration()
    }
}
