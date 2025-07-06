package com.adsearch.application

import com.adsearch.domain.auth.AuthResponse

interface RefreshTokenUseCase {
    fun refreshAccessToken(token: String?): AuthResponse
}
