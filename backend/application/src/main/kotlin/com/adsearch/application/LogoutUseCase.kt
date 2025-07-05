package com.adsearch.application

interface LogoutUseCase {
    fun logout(token: String?)
}
