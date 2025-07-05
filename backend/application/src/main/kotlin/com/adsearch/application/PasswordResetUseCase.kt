package com.adsearch.application

interface PasswordResetUseCase {
    fun requestPasswordReset(username: String)
    fun resetPassword(token: String, newPassword: String)
    fun validateToken(token: String): Boolean
}
