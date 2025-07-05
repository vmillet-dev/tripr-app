package com.adsearch.application

import com.adsearch.domain.command.LoginUserCommand
import com.adsearch.domain.model.AuthResponseDom

interface LoginUseCase {
    fun login(cmd: LoginUserCommand): AuthResponseDom
}
