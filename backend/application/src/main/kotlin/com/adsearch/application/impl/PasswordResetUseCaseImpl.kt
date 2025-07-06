package com.adsearch.application.impl

import com.adsearch.application.PasswordResetUseCase
import com.adsearch.application.annotation.AutoRegister
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.AuthenticationServicePort
import com.adsearch.domain.port.out.ConfigPropertiesPort
import com.adsearch.domain.port.out.EmailServicePort
import com.adsearch.domain.port.out.PasswordResetTokenPersistencePort
import com.adsearch.domain.port.out.UserPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

@AutoRegister
@Suppress("unused")
class PasswordResetUseCaseImpl(
    private val configProperties: ConfigPropertiesPort,
    private val userPersistence: UserPersistencePort,
    private val passwordResetTokenPersistence: PasswordResetTokenPersistencePort,
    private val emailService: EmailServicePort,
    private val authenticationService: AuthenticationServicePort
) : PasswordResetUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Request a password reset for a user
     */
    override fun requestPasswordReset(username: String) {
        val user: UserDom = userPersistence.findByUsername(username)
            ?: throw UserNotFoundException("Password reset request failed - user not found with username: $username")

        // Delete any existing tokens for this user
        passwordResetTokenPersistence.deleteByUserId(user.id)

        // Create a new token
        val expiryDate = Instant.now().plusSeconds(configProperties.getPasswordResetTokenExpiration())
        val resetToken = PasswordResetTokenDom(user.id, UUID.randomUUID().toString(), expiryDate)

        passwordResetTokenPersistence.save(resetToken)

        // Send email
        emailService.sendPasswordResetEmail(user.email, resetToken.token)
    }

    /**
     * Reset a user's password using a token
     */
    override fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenPersistence.findByToken(token)
            ?: throw InvalidTokenException("Password reset failed - token not found")

        if (resetToken.isExpired()) {
            passwordResetTokenPersistence.deleteByToken(resetToken.token)
            throw TokenExpiredException("Password reset failed - expired token for user id: ${resetToken.userId}")
        }

        val user: UserDom = userPersistence.findById(resetToken.userId)
            ?: throw UserNotFoundException("Password reset failed - user not found with user id: ${resetToken.userId}")

        val userWithHashedPwd = user.changePassword(authenticationService.generateHashedPassword(newPassword))
        userPersistence.save(userWithHashedPwd)

        // Delete all tokens for this user
        passwordResetTokenPersistence.deleteByUserId(resetToken.userId)
    }

    /**
     * Validate a password reset token
     */
    override fun validateToken(token: String): Boolean {

        val resetToken = passwordResetTokenPersistence.findByToken(token)
        if (resetToken == null) {
            return false
        }

        if (resetToken.isExpired()) {
            passwordResetTokenPersistence.deleteByToken(resetToken.token)
            return false
        }

        return true
    }
}
