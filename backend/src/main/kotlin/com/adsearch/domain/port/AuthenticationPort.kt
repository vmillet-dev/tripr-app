package com.adsearch.domain.port

import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.PasswordResetToken

interface AuthenticationPort {
    fun authenticate(username: String, password: String): AuthResponse
    fun refreshAccessToken(refreshToken: String): AuthResponse
    fun generateHashedPassword(password: String): String
    fun generatePasswordToken(userId: Long): PasswordResetToken
}
