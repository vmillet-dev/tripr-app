package com.adsearch.domain.port.`in`

interface PasswordResetUseCase {
    fun requestPasswordReset(username: String)
    fun resetPassword(token: String, newPassword: String)
    fun validateToken(token: String): Boolean
}
