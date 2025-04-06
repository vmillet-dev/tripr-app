package com.adsearch.application.service

import com.adsearch.application.usecase.PasswordResetUseCase
import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.common.exception.UserNotFoundException
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.port.EmailServicePort
import com.adsearch.domain.port.PasswordResetTokenPersistencePort
import com.adsearch.domain.port.UserPersistencePort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Service for password reset operations
 */
@Service
class PasswordResetService(
    private val userPersistencePort: UserPersistencePort,
    private val tokenPersistencePort: PasswordResetTokenPersistencePort,
    private val emailService: EmailServicePort,
    private val passwordEncoder: PasswordEncoder,

    @Value("\${password-reset.token-expiration}")
    private val tokenExpiration: Long,

    @Value("\${password-reset.base-url}")
    private val baseUrl: String
) : PasswordResetUseCase {

    private val logger = LoggerFactory.getLogger(PasswordResetService::class.java)

    /**
     * Request a password reset for a user
     */
    override suspend fun requestPasswordReset(username: String) {
        val user = userPersistencePort.findByUsername(username)
            ?: throw UserNotFoundException("User not found with username: $username")

        logger.debug("Processing password reset request for user: ${user.username}")

        // Delete any existing tokens for this user
        tokenPersistencePort.deleteByUserId(user.id)

        // Create a new token
        val token = java.util.UUID.randomUUID().toString()
        val resetToken = PasswordResetToken(
            id = 0,
            userId = user.id,
            token = token,
            expiryDate = Instant.now().plusMillis(tokenExpiration)
        )

        tokenPersistencePort.save(resetToken)
        logger.debug("Created password reset token: {}", resetToken)

        // Generate reset link
        val resetLink = "$baseUrl?token=$token"

        // Send email
        emailService.sendPasswordResetEmail(username, resetLink)
        logger.info("Password reset email sent to: $username")
    }

    /**
     * Reset a user's password using a token
     */
    override suspend fun resetPassword(token: String, newPassword: String) {
        val resetToken = tokenPersistencePort.findByToken(token)
            ?: throw InvalidTokenException("Invalid password reset token")

        logger.debug("Processing password reset with token: {}", resetToken)

        // Check if token is expired
        if (resetToken.expiryDate.isBefore(Instant.now())) {
            tokenPersistencePort.deleteById(resetToken.id)
            throw TokenExpiredException("Password reset token has expired")
        }

        // Check if token has been used
        if (resetToken.used) {
            throw InvalidTokenException("Password reset token has already been used")
        }

        // Find the user
        val user = userPersistencePort.findById(resetToken.userId)
            ?: throw UserNotFoundException("User not found for password reset token")

        // Update the password
        val updatedUser = user.copy(
            password = passwordEncoder.encode(newPassword)
        )

        userPersistencePort.save(updatedUser)
        logger.info("Password reset successful for user: ${user.username}")

        // Mark token as used
        val usedToken = resetToken.copy(used = true)
        tokenPersistencePort.save(usedToken)

        // Delete all tokens for this user
        tokenPersistencePort.deleteByUserId(user.id)
    }

    /**
     * Validate a password reset token
     */
    override suspend fun validateToken(token: String): Boolean {
        val resetToken = tokenPersistencePort.findByToken(token) ?: return false

        // Check if token is expired
        if (resetToken.expiryDate.isBefore(Instant.now())) {
            tokenPersistencePort.deleteById(resetToken.id)
            return false
        }

        // Check if token has been used
        if (resetToken.used) {
            return false
        }

        return true
    }
}
