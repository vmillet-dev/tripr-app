package com.adsearch.domain.port.`in`

import com.adsearch.domain.auth.AuthResponse
import com.adsearch.domain.command.LoginUserCommand

interface LoginUserUseCase {
    fun login(cmd: LoginUserCommand): AuthResponse
}
