package com.adsearch.domain.port.`in`

interface JwtTokenServicePort {
    fun createAccessToken(userId: String, username: String, roles: List<String>): String
    fun validateAccessTokenAndGetUsername(token: String): String?
}
