package com.adsearch.domain.port.out.security

import com.adsearch.domain.model.UserDom

interface TokenGeneratorPort {
    fun generateAccessToken(user: UserDom): String
    fun validateAccessTokenAndGetUsername(token: String): String?
}
