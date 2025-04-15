package com.adsearch.domain.port

import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.PasswordResetToken

interface AuthenticationPort {
    suspend fun authenticate(username: String, password: String): AuthResponse
    suspend fun refreshAccessToken(refreshToken: String): AuthResponse
    suspend fun generateHashedPassword(password: String): String
    suspend fun generatePasswordToken(userId: Long): PasswordResetToken
}
