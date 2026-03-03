package com.adsearch.domain.port.`in`


interface RefreshTokenUseCase {
    fun refreshAccessToken(token: String?): AccessToken

    data class AccessToken(
        val accessToken: String
    )
}
