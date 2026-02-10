package com.adsearch.domain.port.`in`

import com.adsearch.domain.auth.AuthResponse

interface RefreshTokenUseCase {
    fun refreshAccessToken(token: String?): AuthResponse
}
