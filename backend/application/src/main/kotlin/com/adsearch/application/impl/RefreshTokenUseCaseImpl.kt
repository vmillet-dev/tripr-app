package com.adsearch.application.impl

import com.adsearch.application.RefreshTokenUseCase
import com.adsearch.application.annotation.AutoRegister
import com.adsearch.domain.auth.AuthResponse
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.JwtTokenServicePort
import com.adsearch.domain.port.out.RefreshTokenPersistencePort
import com.adsearch.domain.port.out.UserPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@AutoRegister
@Suppress("unused")
class RefreshTokenUseCaseImpl(
    private val refreshTokenPersistence: RefreshTokenPersistencePort,
    private val userPersistence: UserPersistencePort,
    private val jwtTokenService: JwtTokenServicePort
) : RefreshTokenUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun refreshAccessToken(token: String?): AuthResponse {

        if (token == null) {
            throw InvalidTokenException("Token refresh failed - refresh token missing")
        }

        val refreshTokenDom: RefreshTokenDom? = refreshTokenPersistence.findByToken(token)
        if (refreshTokenDom == null) {
            throw InvalidTokenException("Token refresh failed - invalid refresh token provided")
        }

        if (refreshTokenDom.isExpired() || refreshTokenDom.revoked) {
            refreshTokenPersistence.deleteByToken(refreshTokenDom.token)
            throw TokenExpiredException("Token refresh failed - refresh token expired or revoked for user id: ${refreshTokenDom.userId}")
        }

        val user: UserDom = userPersistence.findById(refreshTokenDom.userId)
            ?: throw UserNotFoundException("Token refresh failed - user not found with user id: ${refreshTokenDom.userId}")

        val accessToken: String = jwtTokenService.createAccessToken(user)
        return AuthResponse(accessToken)
    }

}
