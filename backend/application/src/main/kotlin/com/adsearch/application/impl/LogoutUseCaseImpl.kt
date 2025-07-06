package com.adsearch.application.impl

import com.adsearch.application.LogoutUseCase
import com.adsearch.application.annotation.AutoRegister
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.port.out.RefreshTokenPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@AutoRegister
@Suppress("unused")
class LogoutUseCaseImpl(private val refreshTokenPersistence: RefreshTokenPersistencePort) : LogoutUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun logout(token: String?) {

        if (token == null) {
            throw InvalidTokenException("Logout attempted without refresh token")
        }
        refreshTokenPersistence.deleteByToken(token)
    }
}
