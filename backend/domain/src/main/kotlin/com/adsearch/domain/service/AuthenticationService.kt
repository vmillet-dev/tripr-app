package com.adsearch.domain.service

import com.adsearch.domain.annotation.AutoRegister
import com.adsearch.domain.auth.AuthResponse
import com.adsearch.domain.command.LoginUserCommand
import com.adsearch.domain.exception.InvalidCredentialsException
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.model.enums.TokenTypeEnum
import com.adsearch.domain.port.`in`.LoginUserUseCase
import com.adsearch.domain.port.`in`.LogoutUserUseCase
import com.adsearch.domain.port.`in`.RefreshTokenUseCase
import com.adsearch.domain.port.out.ConfigurationProviderPort
import com.adsearch.domain.port.out.persistence.TokenPersistencePort
import com.adsearch.domain.port.out.persistence.UserPersistencePort
import com.adsearch.domain.port.out.security.AuthenticationProviderPort
import com.adsearch.domain.port.out.security.TokenGeneratorPort
import java.time.Instant
import java.util.UUID

@AutoRegister
@Suppress("unused")
class AuthenticationService(
    private val authenticationProvider: AuthenticationProviderPort,
    private val tokenGenerator: TokenGeneratorPort,
    private val configurationProvider: ConfigurationProviderPort,
    private val tokenPersistence: TokenPersistencePort,
    private val userPersistence: UserPersistencePort
): LoginUserUseCase, LogoutUserUseCase, RefreshTokenUseCase {

    override fun login(cmd: LoginUserCommand): AuthResponse {

        val user: UserDom
        try {
            val authenticatedUsername = authenticationProvider.authenticate(cmd.username, cmd.password)
            user = userPersistence.findByUsername(authenticatedUsername)!!
        } catch (e: Exception) {
            throw InvalidCredentialsException(
                "Authentication failed for user ${cmd.username} - invalid credentials provided", cause = e
            )
        }

        // Clean up existing refresh tokens
        tokenPersistence.deleteByUserId(user.id, TokenTypeEnum.REFRESH)

        val expiryDate = Instant.now().plusSeconds(configurationProvider.getRefreshTokenExpiration())
        val refreshToken = RefreshTokenDom(user.id, UUID.randomUUID().toString(), expiryDate, false)
        tokenPersistence.save(refreshToken)

        val accessToken: String = tokenGenerator.generateAccessToken(user)

        return AuthResponse(accessToken, refreshToken.token)
    }

    override fun logout(token: String?) {
        if (token == null) {
            throw InvalidTokenException("Logout attempted without refresh token")
        }
        val refreshTokenDom = tokenPersistence.findByToken(token, TokenTypeEnum.REFRESH)
        if (refreshTokenDom != null) {
            tokenPersistence.delete(refreshTokenDom)
        }
    }

    override fun refreshAccessToken(token: String?): AuthResponse {

        if (token == null) {
            throw InvalidTokenException("Token refresh failed - refresh token missing")
        }

        val refreshTokenDom = tokenPersistence.findByToken(token, TokenTypeEnum.REFRESH)
            ?: throw InvalidTokenException("Token refresh failed - invalid refresh token provided")

        if (refreshTokenDom.isExpired() || refreshTokenDom.revoked) {
            tokenPersistence.delete(refreshTokenDom)
            throw TokenExpiredException("Token refresh failed - refresh token expired or revoked for user id: ${refreshTokenDom.userId}")
        }

        val user: UserDom = userPersistence.findById(refreshTokenDom.userId)
            ?: throw UserNotFoundException("Token refresh failed - user not found with user id: ${refreshTokenDom.userId}")

        val accessToken: String = tokenGenerator.generateAccessToken(user)
        return AuthResponse(accessToken)
    }
}
