package com.adsearch.application.service

import com.adsearch.application.usecase.AuthenticationUseCase
import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.common.exception.UserAlreadyExistsException
import com.adsearch.common.exception.UserNotFoundException
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.User
import com.adsearch.domain.port.AuthenticationPort
import com.adsearch.domain.port.EmailServicePort
import com.adsearch.domain.port.PasswordResetTokenPersistencePort
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.UserPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Service for authentication operations
 */
@Service
class AuthenticationService(
    private val authenticationPort: AuthenticationPort,
    private val userPersistencePort: UserPersistencePort,
    private val passwordResetTokenPersistencePort: PasswordResetTokenPersistencePort,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val emailServicePort: EmailServicePort
) : AuthenticationUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Authenticate a user with username and password
     */
    override fun login(username: String, password: String): AuthResponse {
        return authenticationPort.authenticate(username, password)
    }

    /**
     * Refresh an access token using a refresh token
     */
    override fun refreshAccessToken(refreshToken: String?): AuthResponse {
        if (refreshToken == null) {
            LOG.error("Refresh token is missing in cookies")
            throw InvalidTokenException(message = "Refresh token is missing")
        }

        return authenticationPort.refreshAccessToken(refreshToken)
    }

    /**
     * Logout a user by invalidating their refresh tokens
     */
    override fun logout(refreshToken: String?) {
        refreshToken?.let { token ->
            refreshTokenPersistencePort.findByToken(token)?.let { storedToken ->
                refreshTokenPersistencePort.deleteByUserId(storedToken.userId)
            }
        }
    }

    /**
     * Register a new user
     */
    override fun register(authRequest: AuthRequest, email: String) {
        val existingUser = userPersistencePort.findByUsername(authRequest.username)
        if (existingUser != null) {
            throw UserAlreadyExistsException("Username already exists")
        }

        val hashedPassword = authenticationPort.generateHashedPassword(authRequest.password)
        
        User(
            username = authRequest.username,
            password = hashedPassword,
            roles = listOf("USER")
        ).let { user ->
            userPersistencePort.save(user)
        }
    }

    /**
     * Request a password reset for a user
     */
    override fun requestPasswordReset(username: String) {
        val user = userPersistencePort.findByUsername(username)
            ?: throw UserNotFoundException("User not found with username: $username")

        LOG.debug("Processing password reset request for user: ${user.username}")

        // Delete any existing tokens for this user
        passwordResetTokenPersistencePort.deleteByUserId(user.id)

        // Create a new token
        val resetToken = authenticationPort.generatePasswordToken(user.id)
        passwordResetTokenPersistencePort.save(resetToken)
        LOG.debug("Created password reset token: {}", resetToken)

        // Send email
        emailServicePort.sendPasswordResetEmail(username, resetToken.token)
        LOG.info("Password reset email sent to: $username")
    }

    /**
     * Reset a user's password using a token
     */
    override fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenPersistencePort.findByToken(token)
            ?: throw InvalidTokenException("Invalid password reset token")

        LOG.debug("Processing password reset with token: {}", resetToken)

        // Check if token is expired
        if (resetToken.expiryDate.isBefore(Instant.now())) {
            passwordResetTokenPersistencePort.deleteById(resetToken.id)
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
            password = authenticationPort.generateHashedPassword(newPassword)
        )
        userPersistencePort.save(updatedUser)
        LOG.info("Password reset successful for user: ${user.username}")

        // Mark token as used
        val usedToken = resetToken.copy(used = true)
        passwordResetTokenPersistencePort.save(usedToken)

        // Delete all tokens for this user
        passwordResetTokenPersistencePort.deleteByUserId(user.id)
    }

    /**
     * Validate a password reset token
     */
    override fun validateToken(token: String): Boolean {
        val resetToken = passwordResetTokenPersistencePort.findByToken(token) ?: return false

        // Check if token is expired
        if (resetToken.expiryDate.isBefore(Instant.now())) {
            passwordResetTokenPersistencePort.deleteById(resetToken.id)
            return false
        }

        // Check if token has been used
        if (resetToken.used) {
            return false
        }

        return true
    }
}
