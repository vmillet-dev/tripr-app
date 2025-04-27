package com.adsearch.domain.port.api

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User

interface AuthenticationPort {
    fun authenticate(username: String, password: String)
    fun loadAuthenticateUserByUsername(username: String): User
    fun generateHashedPassword(password: String): String
    fun generatePasswordResetToken(userId: Long): PasswordResetToken
    fun generateRefreshToken(userId: Long): RefreshToken
}
