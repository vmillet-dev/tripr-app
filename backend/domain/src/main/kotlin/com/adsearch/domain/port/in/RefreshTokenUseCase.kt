package com.adsearch.domain.port.`in`

import com.adsearch.domain.model.auth.AuthResponse

interface RefreshTokenUseCase {
    fun refreshAccessToken(token: String?): AuthResponse
}
