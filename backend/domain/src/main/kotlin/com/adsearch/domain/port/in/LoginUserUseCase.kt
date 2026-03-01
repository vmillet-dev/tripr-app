package com.adsearch.domain.port.`in`

interface LoginUserUseCase {
    fun login(cmd: LoginUserCommand): LoginUser

    data class LoginUserCommand(
        val username: String,
        val password: String
    )

    data class LoginUser(
        val accessToken: String,
        val refreshToken: String
    )
}
