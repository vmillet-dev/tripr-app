package com.adsearch.domain.port.api

interface JwtTokenServicePort {
    fun createAccessToken(userId: String, username: String, roles: List<String>): String
    fun validateAccessTokenAndGetUsername(token: String): String?
}
