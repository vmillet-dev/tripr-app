package com.adsearch.domain.port.`in`

interface CreateUserUseCase {
    fun createUser(cmd: RegisterUserCommand)

    data class RegisterUserCommand(
        val username: String,
        val email: String,
        var password: String,
    )

}
