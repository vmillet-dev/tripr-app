package com.adsearch.application.service

import com.adsearch.application.usecase.AuthenticationUseCase
import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.common.exception.UserAlreadyExistsException
import com.adsearch.common.exception.UserNotFoundException
import com.adsearch.domain.event.DomainEventPublisher
import com.adsearch.domain.event.auth.AuthenticationEvent
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.PasswordResetResult
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
    private val emailServicePort: EmailServicePort,
    private val eventPublisher: DomainEventPublisher
) : AuthenticationUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Authenticate a user with username and password
     */
    override fun login(username: String, password: String): AuthResponse {
        val response = authenticationPort.authenticate(username, password)
        eventPublisher.publish(AuthenticationEvent.UserLoggedIn(username))
        return response
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
                userPersistencePort.findById(storedToken.userId)?.let { user ->
                    refreshTokenPersistencePort.deleteByUserId(storedToken.userId)
                    eventPublisher.publish(AuthenticationEvent.UserLoggedOut(user.username))
                }
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
            roles = mutableListOf("USER")
        ).let { user ->
            userPersistencePort.save(user)
            eventPublisher.publish(AuthenticationEvent.UserRegistered(user.username))
        }
    }

    /**
     * Request a password reset for a user
     */
    override fun requestPasswordReset(username: String): PasswordResetResult {
        return userPersistencePort.findByUsername(username)?.let { user ->
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
            
            eventPublisher.publish(AuthenticationEvent.PasswordResetRequested(username))
            PasswordResetResult.EmailSent
        } ?: PasswordResetResult.UserNotFound(username)
    }

    /**
     * Reset a user's password using a token
     */
    override fun resetPassword(token: String, newPassword: String): PasswordResetResult {
        return passwordResetTokenPersistencePort.findByToken(token)?.let { resetToken ->
            LOG.debug("Processing password reset with token: {}", resetToken)

            // Check if token is expired
            if (resetToken.expiryDate.isBefore(Instant.now())) {
                passwordResetTokenPersistencePort.deleteById(resetToken.id)
                return PasswordResetResult.TokenValidation.Expired
            }

            // Check if token has been used
            if (resetToken.used) {
                return PasswordResetResult.TokenValidation.Used
            }

            // Find the user
            return userPersistencePort.findById(resetToken.userId)?.let { user ->
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
                
                eventPublisher.publish(AuthenticationEvent.PasswordReset(user.username))
                PasswordResetResult.Success
            } ?: PasswordResetResult.UserNotFound("User not found for password reset token")
        } ?: PasswordResetResult.TokenValidation.Invalid
    }

    /**
     * Validate a password reset token
     */
    override fun validateToken(token: String): PasswordResetResult.TokenValidation {
        return passwordResetTokenPersistencePort.findByToken(token)?.let { resetToken ->
            // Check if token is expired
            if (resetToken.expiryDate.isBefore(Instant.now())) {
                passwordResetTokenPersistencePort.deleteById(resetToken.id)
                return PasswordResetResult.TokenValidation.Expired
            }

            // Check if token has been used
            if (resetToken.used) {
                return PasswordResetResult.TokenValidation.Used
            }

            PasswordResetResult.TokenValidation.Valid
        } ?: PasswordResetResult.TokenValidation.Invalid
    }
}
