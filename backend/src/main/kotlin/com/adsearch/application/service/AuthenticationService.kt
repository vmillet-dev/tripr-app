package com.adsearch.application.service

import com.adsearch.application.usecase.AuthenticationUseCase
import com.adsearch.common.exception.functional.InvalidCredentialsException
import com.adsearch.common.exception.functional.InvalidTokenException
import com.adsearch.common.exception.functional.TokenExpiredException
import com.adsearch.common.exception.functional.UserAlreadyExistsException
import com.adsearch.common.exception.functional.UserNotFoundException
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.api.AuthenticationPort
import com.adsearch.domain.port.api.EmailServicePort
import com.adsearch.domain.port.api.JwtTokenServicePort
import com.adsearch.domain.port.spi.PasswordResetTokenPersistencePort
import com.adsearch.domain.port.spi.RefreshTokenPersistencePort
import com.adsearch.domain.port.spi.UserPersistencePort
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
    private val userPersistence: UserPersistencePort,
    private val passwordResetTokenPersistence: PasswordResetTokenPersistencePort,
    private val refreshTokenPersistence: RefreshTokenPersistencePort,
    private val emailService: EmailServicePort,
    private val jwtTokenService: JwtTokenServicePort,
) : AuthenticationUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Authenticate a user with username and password
     */
    override fun login(username: String, password: String): AuthResponse {
        try {
            authenticationPort.authenticate(username, password)
        } catch (_: Exception) {
            throw InvalidCredentialsException()
        }

        val user: User = authenticationPort.loadAuthenticateUserByUsername(username)

        refreshTokenPersistence.deleteByUserId(user.id)
        val refreshToken: RefreshToken = authenticationPort.generateRefreshToken(user.id)
        refreshTokenPersistence.save(refreshToken)

        val accessToken: String = jwtTokenService.createAccessToken(user.id.toString(), user.username, user.roles)

        return AuthResponse(
            accessToken = accessToken,
            username = user.username,
            roles = user.roles,
            refreshToken = refreshToken
        )
    }

    /**
     * Register a new user
     */
    override fun register(authRequest: AuthRequest, email: String) {
        val existingUser = userPersistence.findByUsername(authRequest.username)
        if (existingUser != null) {
            throw UserAlreadyExistsException("Username already exists")
        }

        val hashedPassword = authenticationPort.generateHashedPassword(authRequest.password)

        User(
            username = authRequest.username,
            password = hashedPassword,
            roles = listOf("USER")
        ).let { user ->
            userPersistence.save(user)
        }
    }

    /**
     * Refresh an access token using a refresh token
     */
    override fun refreshAccessToken(refreshToken: String?): AuthResponse {
        if (refreshToken == null) {
            LOG.error("Refresh token is missing in cookies")
            throw InvalidTokenException(message = "Refresh token is missing")
        }

        val refreshToken: RefreshToken? = refreshTokenPersistence.findByToken(refreshToken)

        if (refreshToken == null) {
            LOG.warn("Refresh token invalid")
            throw InvalidTokenException()
        }

        if (refreshToken.isExpired() || refreshToken.revoked) {
            refreshTokenPersistence.deleteById(refreshToken.id)
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }

        val user: User = userPersistence.findById(refreshToken.userId)?: throw UserNotFoundException("User not found")
        val authenticateUser: User = authenticationPort.loadAuthenticateUserByUsername(user.username)

        if (user.id != authenticateUser.id) {
            throw InvalidCredentialsException()
        }

        val accessToken: String = jwtTokenService.createAccessToken(user.id.toString(), user.username, user.roles)

        return AuthResponse(
            accessToken = accessToken,
            username = user.username,
            roles = user.roles,
            refreshToken = refreshToken
        )
    }

    /**
     * Logout a user by invalidating their refresh tokens
     */
    override fun logout(refreshToken: String?) {
        refreshToken?.let { token ->
            refreshTokenPersistence.findByToken(token)?.let { storedToken ->
                refreshTokenPersistence.deleteByUserId(storedToken.userId)
            }
        }
    }

    /**
     * Request a password reset for a user
     */
    override fun requestPasswordReset(username: String) {
        val user = userPersistence.findByUsername(username)
            ?: throw UserNotFoundException("User not found with username: $username")

        LOG.debug("Processing password reset request for user: ${user.username}")

        // Delete any existing tokens for this user
        passwordResetTokenPersistence.deleteByUserId(user.id)

        // Create a new token
        val resetToken = authenticationPort.generatePasswordResetToken(user.id)
        passwordResetTokenPersistence.save(resetToken)
        LOG.debug("Created password reset token: {}", resetToken)

        // Send email
        emailService.sendPasswordResetEmail(username, resetToken.token)
        LOG.info("Password reset email sent to: $username")
    }

    /**
     * Reset a user's password using a token
     */
    override fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenPersistence.findByToken(token)
            ?: throw InvalidTokenException("Invalid password reset token")

        LOG.debug("Processing password reset with token: {}", resetToken)

        // Check if token is expired
        if (resetToken.expiryDate.isBefore(Instant.now())) {
            passwordResetTokenPersistence.deleteById(resetToken.id)
            throw TokenExpiredException("Password reset token has expired")
        }

        // Check if token has been used
        if (resetToken.used) {
            throw InvalidTokenException("Password reset token has already been used")
        }

        // Find the user
        val user = userPersistence.findById(resetToken.userId)
            ?: throw UserNotFoundException("User not found for password reset token")

        // Update the password
        val updatedUser = user.copy(
            password = authenticationPort.generateHashedPassword(newPassword)
        )
        userPersistence.save(updatedUser)
        LOG.info("Password reset successful for user: ${user.username}")

        // Mark token as used
        val usedToken = resetToken.copy(used = true)
        passwordResetTokenPersistence.save(usedToken)

        // Delete all tokens for this user
        passwordResetTokenPersistence.deleteByUserId(user.id)
    }

    /**
     * Validate a password reset token
     */
    override fun validateToken(token: String): Boolean {
        val resetToken = passwordResetTokenPersistence.findByToken(token) ?: return false

        // Check if token is expired
        if (resetToken.expiryDate.isBefore(Instant.now())) {
            passwordResetTokenPersistence.deleteById(resetToken.id)
            return false
        }

        // Check if token has been used
        if (resetToken.used) {
            return false
        }

        return true
    }
}
