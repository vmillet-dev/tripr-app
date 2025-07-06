package com.adsearch.application.impl

import com.adsearch.application.LoginUseCase
import com.adsearch.application.annotation.AutoRegister
import com.adsearch.domain.command.LoginUserCommand
import com.adsearch.domain.exception.InvalidCredentialsException
import com.adsearch.domain.model.AuthResponseDom
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.AuthenticationServicePort
import com.adsearch.domain.port.`in`.JwtTokenServicePort
import com.adsearch.domain.port.out.ConfigPropertiesPort
import com.adsearch.domain.port.out.RefreshTokenPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

@AutoRegister
@Suppress("unused")
class LoginUseCaseImpl(
    private val configProperties: ConfigPropertiesPort,
    private val authenticationService: AuthenticationServicePort,
    private val refreshTokenPersistence: RefreshTokenPersistencePort,
    private val jwtTokenService: JwtTokenServicePort
) : LoginUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }


    override fun login(cmd: LoginUserCommand): AuthResponseDom {

        val user: UserDom
        try {
            user = authenticationService.authenticate(cmd.username, cmd.password)
        } catch (e: Exception) {
            throw InvalidCredentialsException(
                "Authentication failed for user ${cmd.username} - invalid credentials provided", cause = e
            )
        }
        // Clean up existing refresh tokens
        refreshTokenPersistence.deleteByUserId(user.id)

        val expiryDate = Instant.now().plusSeconds(configProperties.getRefreshTokenExpiration())
        val refreshToken = RefreshTokenDom(user.id, UUID.randomUUID().toString(), expiryDate, false)
        refreshTokenPersistence.save(refreshToken)

        val accessToken: String = jwtTokenService.createAccessToken(user)

        return AuthResponseDom(accessToken, refreshToken.token)
    }
}
