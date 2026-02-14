package com.adsearch.domain.port.out.authentication

import com.adsearch.domain.model.UserDom

interface TokenGeneratorPort {
    fun generateAccessToken(user: UserDom): String
    fun validateAccessTokenAndGetUsername(token: String): String?
    fun getAuthoritiesFromToken(token: String): List<String>
}
