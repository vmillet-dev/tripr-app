package com.adsearch.domain.model

/**
 * Sealed class representing password reset operation results
 */
sealed class PasswordResetResult {
    /**
     * Successfully sent password reset email
     */
    data object EmailSent : PasswordResetResult()

    /**
     * Successfully reset password
     */
    data object Success : PasswordResetResult()

    /**
     * Token validation result
     */
    sealed class TokenValidation : PasswordResetResult() {
        data object Valid : TokenValidation()
        data object Invalid : TokenValidation()
        data object Expired : TokenValidation()
        data object Used : TokenValidation()
    }

    /**
     * User not found
     */
    data class UserNotFound(
        val username: String
    ) : PasswordResetResult()
}
