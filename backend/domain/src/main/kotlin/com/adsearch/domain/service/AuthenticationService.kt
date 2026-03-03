package com.adsearch.domain.service

import com.adsearch.domain.annotation.AutoRegister
import com.adsearch.domain.exception.InvalidCredentialsException
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.Token
import com.adsearch.domain.model.User
import com.adsearch.domain.port.`in`.LoginUserUseCase
import com.adsearch.domain.port.`in`.LogoutUserUseCase
import com.adsearch.domain.port.`in`.RefreshTokenUseCase
import com.adsearch.domain.port.out.ConfigurationProviderPort
import com.adsearch.domain.port.out.authentication.AuthenticationProviderPort
import com.adsearch.domain.port.out.authentication.TokenGeneratorPort
import com.adsearch.domain.port.out.persistence.TokenPersistencePort
import com.adsearch.domain.port.out.persistence.UserPersistencePort
import com.adsearch.domain.port.out.persistence.deleteRefreshTokenByToken
import com.adsearch.domain.port.out.persistence.findRefreshTokenByToken
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64

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

        val expiryDate = Instant.now().plusSeconds(configurationProvider.getRefreshTokenExpiration())
        val rawToken = generateRefreshToken()
        val refreshToken = RefreshToken(user.id, hashRefreshToken(rawToken), expiryDate, false)

        tokenPersistence.save(refreshToken)

        val accessToken: String = tokenGenerator.generateAccessToken(user)

        return LoginUserUseCase.LoginUser(accessToken, rawToken)
    }

    override fun logout(token: String?) {
        if (token == null) {
            throw InvalidTokenException("Logout attempted without refresh token")
        }
        tokenPersistence.deleteRefreshTokenByToken(hashRefreshToken(token))
    }

    override fun refreshAccessToken(token: String?): RefreshTokenUseCase.AccessToken {

        if (token == null) {
            throw InvalidTokenException("Token refresh failed - refresh token missing")
        }

        val refreshToken: Token = tokenPersistence.findRefreshTokenByToken(hashRefreshToken(token))
            ?: throw InvalidTokenException("Token refresh failed - invalid refresh token provided")

        if (refreshToken.isExpired() || refreshToken.revoked) {
            tokenPersistence.deleteRefreshTokenByToken(hashRefreshToken(token))
            throw TokenExpiredException("Token refresh failed - refresh token expired or revoked for user id: ${refreshToken.userId}")
        }

        val user: User = userPersistence.findById(refreshToken.userId)
            ?: throw UserNotFoundException("Token refresh failed - user not found with user id: ${refreshToken.userId}")

        val accessToken: String = tokenGenerator.generateAccessToken(user)
        return RefreshTokenUseCase.AccessToken(accessToken)
    }

    private fun hashRefreshToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-512")
        val hashBytes = digest.digest(token.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun generateRefreshToken(byteLength: Int = 64): String {
        val bytes = ByteArray(byteLength)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
