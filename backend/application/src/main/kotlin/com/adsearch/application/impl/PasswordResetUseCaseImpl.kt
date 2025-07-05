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
        LOG.info("Password reset request initiated for user $username")

        val user: UserDom = userPersistence.findByUsername(username)
            ?: throw UserNotFoundException("Password reset request failed - user not found with username: $username")

        // Delete any existing tokens for this user
        passwordResetTokenPersistence.deleteByUserId(user.id)

        // Create a new token
        val expiryDate = Instant.now().plusSeconds(configProperties.getPasswordResetTokenExpiration())
        val resetToken = PasswordResetTokenDom(user.id, UUID.randomUUID().toString(), expiryDate, false)

        passwordResetTokenPersistence.save(resetToken)
        LOG.info("Password reset ${resetToken.token} successful for user $username")

        // Send email
        emailService.sendPasswordResetEmail(user.email, resetToken.token)
        LOG.info("Password reset email sent to: $username")
    }

    /**
     * Reset a user's password using a token
     */
    override fun resetPassword(token: String, newPassword: String) {
        LOG.info("Password reset attempt with token $token")

        val resetToken = passwordResetTokenPersistence.findByToken(token)
            ?: throw InvalidTokenException("Password reset failed - invalid token provided")

        LOG.debug("Password reset token found for user id: ${resetToken.userId}")

        if (resetToken.isExpired() || resetToken.used) {
            passwordResetTokenPersistence.deleteByToken(resetToken.token)
            throw TokenExpiredException("Password reset failed - invalid token for user: ${resetToken.userId}")
        }

        val user: UserDom = userPersistence.findById(resetToken.userId)
            ?: throw UserNotFoundException("Password reset failed - user not found with user id: ${resetToken.userId}")

        val userWithHashedPwd = user.changePassword(authenticationService.generateHashedPassword(newPassword))
        userPersistence.save(userWithHashedPwd)

        // Delete all tokens for this user
        passwordResetTokenPersistence.deleteByUserId(resetToken.userId)

        LOG.info("Password reset completed successfully for user ${resetToken.userId}")
    }

    /**
     * Validate a password reset token
     */
    override fun validateToken(token: String): Boolean {
        LOG.debug("Token validation attempt with token $token")

        val resetToken = passwordResetTokenPersistence.findByToken(token)
        if (resetToken == null) {
            LOG.debug("Token validation failed - token not found")
            return false
        }

        if (resetToken.isExpired() || resetToken.used) {
            LOG.debug("Token validation failed - token $token expired for user id: ${resetToken.userId}")
            passwordResetTokenPersistence.deleteByToken(resetToken.token)
            return false
        }

        LOG.debug("Token validation successful for user id: ${resetToken.userId}")
        return true
    }
}
