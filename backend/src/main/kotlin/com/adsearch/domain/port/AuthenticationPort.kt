package com.adsearch.domain.port

import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse

interface AuthenticationPort {
    suspend fun authenticate(username: String, password: String): AuthResponse
    suspend fun register(authRequest: AuthRequest)
    suspend fun refreshAccessToken(refreshToken: String): AuthResponse
    suspend fun logout(refreshToken: String)
}
