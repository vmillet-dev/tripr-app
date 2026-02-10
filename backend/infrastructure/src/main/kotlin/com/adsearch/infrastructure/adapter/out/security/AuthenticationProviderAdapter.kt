package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.port.out.security.AuthenticationProviderPort
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class AuthenticationProviderAdapter(
    private val authenticationManager: AuthenticationManager,
) : AuthenticationProviderPort {

    override fun authenticate(username: String, password: String): String {
        val authToken = UsernamePasswordAuthenticationToken(username, password)
        val authentication: Authentication = authenticationManager.authenticate(authToken)

        return authentication.name
    }
}
