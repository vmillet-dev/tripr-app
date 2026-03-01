package com.adsearch.domain.service

import com.adsearch.domain.annotation.AutoRegister
import com.adsearch.domain.enums.TokenTypeEnum
import com.adsearch.domain.exception.InvalidCredentialsException
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.`in`.LoginUserUseCase
import com.adsearch.domain.port.`in`.LogoutUserUseCase
import com.adsearch.domain.port.`in`.RefreshTokenUseCase
import com.adsearch.domain.port.out.ConfigurationProviderPort
import com.adsearch.domain.port.out.authentication.AuthenticationProviderPort
import com.adsearch.domain.port.out.authentication.TokenGeneratorPort
import com.adsearch.domain.port.out.persistence.TokenPersistencePort
import com.adsearch.domain.port.out.persistence.UserPersistencePort
import java.time.Instant
import java.util.*

@AutoRegister
@Suppress("unused")
class AuthenticationService(
    private val authenticationProvider: AuthenticationProviderPort,
    private val tokenGenerator: TokenGeneratorPort,
    private val configurationProvider: ConfigurationProviderPort,
    private val tokenPersistence: TokenPersistencePort,
    private val userPersistence: UserPersistencePort
) : LoginUserUseCase, LogoutUserUseCase, RefreshTokenUseCase {

    override fun login(cmd: LoginUserUseCase.LoginUserCommand): LoginUserUseCase.LoginUser {

        val user: User
        try {
            val authenticatedUsername = authenticationProvider.authenticate(cmd.username, cmd.password)
            user = userPersistence.findByUsername(authenticatedUsername)!!
        } catch (e: Exception) {
            throw InvalidCredentialsException("Authentication failed for user ${cmd.username} - invalid credentials provided", cause = e)
        }

        // Clean up existing refresh tokens
        tokenPersistence.deleteRefreshTokenByUser(user)

        val expiryDate = Instant.now().plusSeconds(configurationProvider.getRefreshTokenExpiration())
        val refreshToken = RefreshToken(user.id, UUID.randomUUID().toString(), expiryDate, false)
        tokenPersistence.save(refreshToken)

        val accessToken: String = tokenGenerator.generateAccessToken(user)

        return LoginUserUseCase.LoginUser(accessToken, refreshToken.token)
    }

    override fun logout(token: String?) {
        if (token == null) {
            throw InvalidTokenException("Logout attempted without refresh token")
        }
        val refreshToken = tokenPersistence.findByToken(token, TokenTypeEnum.REFRESH)
        if (refreshToken != null) {
            tokenPersistence.delete(refreshToken)
        }
    }

    override fun refreshAccessToken(token: String?): RefreshTokenUseCase.AccessToken {

        if (token == null) {
            throw InvalidTokenException("Token refresh failed - refresh token missing")
        }

        val refreshToken = tokenPersistence.findByToken(token, TokenTypeEnum.REFRESH)
            ?: throw InvalidTokenException("Token refresh failed - invalid refresh token provided")

        if (refreshToken.isExpired() || refreshToken.revoked) {
            tokenPersistence.delete(refreshToken)
            throw TokenExpiredException("Token refresh failed - refresh token expired or revoked for user id: ${refreshToken.userId}")
        }

        val user: User = userPersistence.findById(refreshToken.userId)
            ?: throw UserNotFoundException("Token refresh failed - user not found with user id: ${refreshToken.userId}")

        val accessToken: String = tokenGenerator.generateAccessToken(user)
        return RefreshTokenUseCase.AccessToken(accessToken)
    }
}
