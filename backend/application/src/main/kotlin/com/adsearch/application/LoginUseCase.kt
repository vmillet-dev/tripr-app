package com.adsearch.application

import com.adsearch.domain.auth.AuthResponse
import com.adsearch.domain.command.LoginUserCommand

interface LoginUseCase {
    fun login(cmd: LoginUserCommand): AuthResponse
}
