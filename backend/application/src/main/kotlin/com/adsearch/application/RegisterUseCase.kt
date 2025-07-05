package com.adsearch.application

import com.adsearch.domain.command.RegisterUserCommand

interface RegisterUseCase {
    fun register(cmd: RegisterUserCommand)
}
