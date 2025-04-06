package com.adsearch.application.service

import com.adsearch.application.usecase.AuthenticationUseCase
import com.adsearch.common.exception.InvalidCredentialsException
import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.UserAlreadyExistsException
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.port.AuthenticationPort
import com.adsearch.domain.port.UserPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

/**
 * Service for authentication operations
 */
@Service
class AuthenticationService(
    private val authenticationPort: AuthenticationPort,
    private val userPersistencePort: UserPersistencePort,
) : AuthenticationUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Authenticate a user with username and password
     */
    override suspend fun login(authRequest: AuthRequest): AuthResponse {
        try {
            return authenticationPort.authenticate(authRequest.username, authRequest.password)
        } catch (ex: BadCredentialsException) {
            throw InvalidCredentialsException()
        }
    }

    /**
     * Refresh an access token using a refresh token
     */
    override suspend fun refreshAccessToken(refreshToken: String?): AuthResponse {
        if (refreshToken == null) {
            LOG.error("Refresh token is missing in cookies")
            throw InvalidTokenException(message = "Refresh token is missing")
        }

        return authenticationPort.refreshAccessToken(refreshToken)
    }

    /**
     * Logout a user by invalidating their refresh tokens
     */
    override suspend fun logout(refreshToken: String?) {
        if (refreshToken != null) {
            authenticationPort.logout(refreshToken)
        }
    }

    /**
     * Register a new user
     */
    override suspend fun register(authRequest: AuthRequest, email: String) {
        val existingUser = userPersistencePort.findByUsername(authRequest.username)
        if (existingUser != null) {
            throw UserAlreadyExistsException("Username already exists")
        }

        authenticationPort.register(authRequest)
    }
}
