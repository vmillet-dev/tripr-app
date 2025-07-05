package com.adsearch.domain.port.`in`

import com.adsearch.domain.model.UserDom

interface JwtTokenServicePort {
    fun createAccessToken(user: UserDom): String
    fun validateAccessTokenAndGetUsername(token: String): String?
}
