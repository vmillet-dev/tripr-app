package com.adsearch.application.impl

import com.adsearch.application.AuthenticationUseCase
import com.adsearch.common.exception.functional.EmailAlreadyExistsException
import com.adsearch.common.exception.functional.InvalidCredentialsException
import com.adsearch.common.exception.functional.InvalidTokenException
import com.adsearch.common.exception.functional.TokenExpiredException
import com.adsearch.common.exception.functional.UserNotFoundException
import com.adsearch.common.exception.functional.UsernameAlreadyExistsException
import com.adsearch.domain.model.AuthResponseDom
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.api.AuthenticationServicePort
import com.adsearch.domain.port.api.EmailServicePort
import com.adsearch.domain.port.api.JwtTokenServicePort
import com.adsearch.domain.port.spi.PasswordResetTokenPersistencePort
import com.adsearch.domain.port.spi.RefreshTokenPersistencePort
import com.adsearch.domain.port.spi.UserPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service for authentication operations
 */
@Service
class AuthenticationUseCaseImpl(
    private val authenticationService: AuthenticationServicePort,
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
    override fun login(username: String, password: String): AuthResponseDom {
        try {
            authenticationService.authenticate(username, password)
        } catch (_: Exception) {
            throw InvalidCredentialsException()
        }

        val user: UserDom = authenticationService.loadAuthenticateUserByUsername(username)

        refreshTokenPersistence.deleteByUserId(user.id)
        val refreshToken: RefreshTokenDom = authenticationService.generateRefreshToken(user.id)
        refreshTokenPersistence.save(refreshToken)

        val accessToken: String = jwtTokenService.createAccessToken(user.id.toString(), user.username, user.roles)

        return AuthResponseDom(user, accessToken,refreshToken.token)
    }

    /**
     * Register a new user
     */
    override fun register(user: UserDom) {
        if (userPersistence.findByUsername(user.username) != null) {
            throw UsernameAlreadyExistsException("Username already exists")
        }

        if (userPersistence.findByEmail(user.email) != null) {
            throw EmailAlreadyExistsException("Email already exists")
        }

        val hashedPassword = authenticationService.generateHashedPassword(user.password)

        user.apply { password = hashedPassword }
        userPersistence.save(user)
    }

    /**
     * Refresh an access token using a refresh token
     */
    override fun refreshAccessToken(refreshToken: String?): AuthResponseDom {
        if (refreshToken == null) {
            LOG.error("Refresh token is missing in cookies")
            throw InvalidTokenException(message = "Refresh token is missing")
        }

        val refreshTokenDom: RefreshTokenDom? = refreshTokenPersistence.findByToken(refreshToken)

        if (refreshTokenDom == null) {
            LOG.warn("Refresh token invalid")
            throw InvalidTokenException()
        }

        if (refreshTokenDom.isExpired() || refreshTokenDom.revoked) {
            refreshTokenPersistence.deleteById(refreshTokenDom.id)
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }

        val user: UserDom = userPersistence.findById(refreshTokenDom.userId)?: throw UserNotFoundException("User not found")
        val authenticateUser: UserDom = authenticationService.loadAuthenticateUserByUsername(user.username)

        if (user.id != authenticateUser.id) {
            throw InvalidCredentialsException()
        }

        val accessToken: String = jwtTokenService.createAccessToken(user.id.toString(), user.username, user.roles)

        return AuthResponseDom(authenticateUser, accessToken)
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
        val resetToken = authenticationService.generatePasswordResetToken(user.id)
        passwordResetTokenPersistence.save(resetToken)
        LOG.debug("Created password reset token: {}", resetToken)

        // Send email
        emailService.sendPasswordResetEmail(user.email, resetToken.token)
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
        if (resetToken.isExpired()) {
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
        user.apply {
            password = authenticationService.generateHashedPassword(newPassword)
        }
        userPersistence.save(user)
        LOG.info("Password reset successful for user: ${user.username}")

        // Delete all tokens for this user
        passwordResetTokenPersistence.deleteByUserId(user.id)
    }

    /**
     * Validate a password reset token
     */
    override fun validateToken(token: String): Boolean {
        val resetToken = passwordResetTokenPersistence.findByToken(token) ?: return false

        // Check if token is expired
        if (resetToken.isExpired()) {
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
