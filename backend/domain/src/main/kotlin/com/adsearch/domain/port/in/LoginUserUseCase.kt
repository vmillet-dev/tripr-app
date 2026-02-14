package com.adsearch.domain.port.`in`

import com.adsearch.domain.model.auth.AuthResponse
import com.adsearch.domain.model.command.LoginUserCommand

interface LoginUserUseCase {
    fun login(cmd: LoginUserCommand): AuthResponse
}
