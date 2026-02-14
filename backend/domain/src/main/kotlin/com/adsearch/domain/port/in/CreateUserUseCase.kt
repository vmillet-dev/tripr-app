package com.adsearch.domain.port.`in`

import com.adsearch.domain.model.command.RegisterUserCommand

interface CreateUserUseCase {
    fun createUser(cmd: RegisterUserCommand)
}
