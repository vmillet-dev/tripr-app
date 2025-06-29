package com.adsearch.domain.port.`in`

import com.adsearch.domain.model.UserDom

interface AuthenticationServicePort {
    fun authenticate(username: String, password: String)
    fun loadAuthenticateUserByUsername(username: String): UserDom
    fun generateHashedPassword(password: String): String
    fun getPasswordResetTokenExpiration(): Long
    fun getRefreshTokenExpiration(): Long
}
