package com.adsearch.domain.port.out.authentication

import com.adsearch.domain.model.User

interface TokenGeneratorPort {
    fun generateAccessToken(user: User): String
    fun validateAccessTokenAndGetUsername(token: String): String?
    fun getAuthoritiesFromToken(token: String): List<String>
}
