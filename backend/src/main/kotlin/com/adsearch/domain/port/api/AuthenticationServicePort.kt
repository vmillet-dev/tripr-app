package com.adsearch.domain.port.api

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom

interface AuthenticationServicePort {
    fun authenticate(username: String, password: String)
    fun loadAuthenticateUserByUsername(username: String): UserDom
    fun generateHashedPassword(password: String): String
    fun generatePasswordResetToken(userId: Long): PasswordResetTokenDom
    fun generateRefreshToken(userId: Long): RefreshTokenDom
}
