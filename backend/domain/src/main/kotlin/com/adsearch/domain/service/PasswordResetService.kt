package com.adsearch.domain.service

import com.adsearch.domain.annotation.AutoRegister
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.Token
import com.adsearch.domain.model.User
import com.adsearch.domain.port.`in`.PasswordResetUseCase
import com.adsearch.domain.port.out.ConfigurationProviderPort
import com.adsearch.domain.port.out.authentication.PasswordEncoderPort
import com.adsearch.domain.port.out.notification.EmailServicePort
import com.adsearch.domain.port.out.persistence.TokenPersistencePort
import com.adsearch.domain.port.out.persistence.UserPersistencePort
import com.adsearch.domain.port.out.persistence.deletePasswordResetTokenByToken
import com.adsearch.domain.port.out.persistence.deletePasswordResetTokenByUser
import com.adsearch.domain.port.out.persistence.findPasswordResetTokenByToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

@AutoRegister
@Suppress("unused")
class PasswordResetService(
    private val configurationProvider: ConfigurationProviderPort,
    private val emailService: EmailServicePort,
    private val passwordEncoder: PasswordEncoderPort,
    private val userPersistence: UserPersistencePort,
    private val tokenPersistence: TokenPersistencePort

) : PasswordResetUseCase {
    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Request a password reset for a user
     */
    override fun requestPasswordReset(username: String) {
        val user: User = userPersistence.findByUsername(username)
            ?: throw UserNotFoundException("Password reset request failed - user not found with username: $username")

        // Delete any existing tokens for this user
        tokenPersistence.deletePasswordResetTokenByUser(user)

        // Create a new token
        val expiryDate = Instant.now().plusSeconds(configurationProvider.getPasswordResetTokenExpiration())
        val resetToken = PasswordResetToken(user.id, UUID.randomUUID().toString(), expiryDate)

        tokenPersistence.save(resetToken)

        // Send email
        emailService.sendPasswordResetEmail(user.email, resetToken.token)
    }

    /**
     * Reset a user's password using a token
     */
    override fun resetPassword(token: String, newPassword: String) {
        val resetToken: Token = tokenPersistence.findPasswordResetTokenByToken(token)
            ?: throw InvalidTokenException("Password reset failed - token not found")

        if (resetToken.isExpired()) {
            tokenPersistence.deletePasswordResetTokenByToken(resetToken.token)
            throw TokenExpiredException("Password reset failed - expired token for user id: ${resetToken.userId}")
        }

        val user: User = userPersistence.findById(resetToken.userId)
            ?: throw UserNotFoundException("Password reset failed - user not found with user id: ${resetToken.userId}")

        val userWithHashedPwd = user.changePassword(passwordEncoder.encode(newPassword))
        userPersistence.save(userWithHashedPwd)

        // Delete all tokens for this user
        tokenPersistence.deletePasswordResetTokenByUser(user)
    }

    /**
     * Validate a password reset token
     */
    override fun validateToken(token: String): Boolean {

        val resetToken: Token = tokenPersistence.findPasswordResetTokenByToken(token) ?: return false

        if (resetToken.isExpired()) {
            tokenPersistence.deletePasswordResetTokenByToken(resetToken.token)
            return false
        }

        return true
    }
}
