package com.adsearch.application

import com.adsearch.domain.model.AuthResponseDom

interface RefreshTokenUseCase {
    fun refreshAccessToken(token: String?): AuthResponseDom
}
