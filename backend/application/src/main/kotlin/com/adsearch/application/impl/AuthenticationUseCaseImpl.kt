package com.adsearch.application.impl

import com.adsearch.application.AuthenticationUseCase
import com.adsearch.domain.command.LoginUserCommand
import com.adsearch.domain.command.RegisterUserCommand
import com.adsearch.domain.exception.EmailAlreadyExistsException
import com.adsearch.domain.exception.InvalidCredentialsException
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.exception.UsernameAlreadyExistsException
import com.adsearch.domain.model.AuthResponseDom
import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.AuthenticationServicePort
import com.adsearch.domain.port.`in`.JwtTokenServicePort
import com.adsearch.domain.port.out.ConfigPropertiesPort
import com.adsearch.domain.port.out.EmailServicePort
import com.adsearch.domain.port.out.PasswordResetTokenPersistencePort
import com.adsearch.domain.port.out.RefreshTokenPersistencePort
import com.adsearch.domain.port.out.UserPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

/**
 * Service for authentication operations
 */
class AuthenticationUseCaseImpl(
    private val configProperties: ConfigPropertiesPort,
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
    override fun login(loginCommand: LoginUserCommand): AuthResponseDom {
        LOG.info("Login attempt for user ${loginCommand.username}")

        val user: UserDom
        try {
            user = authenticationService.authenticate(loginCommand.username, loginCommand.password)
            LOG.debug("User ${loginCommand.username} authentication successful ")
        } catch (e: Exception) {
            throw InvalidCredentialsException(
                "Authentication failed for user ${loginCommand.username} - invalid credentials provided",
                cause = e
            )
        }
            // Clean up existing refresh tokens
            refreshTokenPersistence.deleteByUser(user)

            val expiryDate = Instant.now().plusSeconds(configProperties.getRefreshTokenExpiration())
            val refreshToken = RefreshTokenDom(user, UUID.randomUUID().toString(), expiryDate, false)

            refreshTokenPersistence.save(refreshToken)
            LOG.debug("Refresh token ${refreshToken.token} created for user ${loginCommand.username}")

            val accessToken: String = jwtTokenService.createAccessToken(user)
            LOG.debug("Access token $accessToken created for user ${loginCommand.username}")

            LOG.info("Login successful for user ${loginCommand.username}")
            return AuthResponseDom(user, accessToken, refreshToken.token)
    }

    /**
     * Register a new user
     */
    override fun register(registerCommand: RegisterUserCommand) {
        LOG.info("Registration attempt initiated for user ${registerCommand.username}")

        if (userPersistence.findByUsername(registerCommand.username) != null) {
            throw UsernameAlreadyExistsException("Registration failed - username ${registerCommand.username} already exists")
        }

        if (userPersistence.findByEmail(registerCommand.email) != null) {
            throw EmailAlreadyExistsException("Registration failed - email ${registerCommand.email} already exists")
        }

        registerCommand.apply { password = authenticationService.generateHashedPassword(registerCommand.password) }
        userPersistence.save(UserDom.register(registerCommand))

        LOG.info("User ${registerCommand.username} registration successful")
    }

    /**
     * Refresh an access token using a refresh token
     */
    override fun refreshAccessToken(refreshToken: String?): AuthResponseDom {
        LOG.debug("Access token refresh attempt with refresh token $refreshToken")

        if (refreshToken == null) {
            throw InvalidTokenException("Token refresh failed - refresh token missing")
        }

        val refreshTokenDom: RefreshTokenDom? = refreshTokenPersistence.findByToken(refreshToken)
        if (refreshTokenDom == null) {
            throw InvalidTokenException("Token refresh failed - invalid refresh token provided")
        }

        if (refreshTokenDom.isExpired() || refreshTokenDom.revoked) {
            refreshTokenPersistence.deleteByToken(refreshTokenDom.token)
            throw TokenExpiredException("Token refresh failed - refresh token expired or revoked for username: ${refreshTokenDom.user.username}")
        }

        val user: UserDom = userPersistence.findByUsername(refreshTokenDom.user.username)
            ?: throw UserNotFoundException("Token refresh failed - user not found with username: ${refreshTokenDom.user.username}")

        val accessToken: String = jwtTokenService.createAccessToken(user)
        LOG.debug("New access token $accessToken generated for user ${user.username}")

        LOG.info("Token refresh successful for username: ${user.username}")
        return AuthResponseDom(user, accessToken)
    }

    /**
     * Logout a user by invalidating their refresh tokens
     */
    override fun logout(refreshToken: String?) {
        LOG.debug("Logout attempt initiated with refresh token $refreshToken")

        if (refreshToken == null) {
            throw InvalidTokenException("Logout attempted without refresh token")
        }
        refreshTokenPersistence.deleteByToken(refreshToken)
    }

    /**
     * Request a password reset for a user
     */
    override fun requestPasswordReset(username: String) {
        LOG.info("Password reset request initiated for user $username")

        val user: UserDom = userPersistence.findByUsername(username)
            ?: throw UserNotFoundException("Password reset request failed - user not found with username: $username")

        // Delete any existing tokens for this user
        passwordResetTokenPersistence.deleteByUser(user)

        // Create a new token
        val expiryDate = Instant.now().plusSeconds(configProperties.getPasswordResetTokenExpiration())
        val resetToken = PasswordResetTokenDom(user, UUID.randomUUID().toString(), expiryDate, false)

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

        LOG.debug("Password reset token found for username: ${resetToken.user.username}")

        if (resetToken.isExpired() || resetToken.used) {
            passwordResetTokenPersistence.deleteByToken(resetToken.token)
            throw TokenExpiredException("Password reset failed - invalid token for user: ${resetToken.user.username}")
        }

        // Update the password
        userPersistence.updatePassword(
            resetToken.user.username,
            authenticationService.generateHashedPassword(newPassword)
        )

        // Delete all tokens for this user
        passwordResetTokenPersistence.deleteByUser(resetToken.user)

        LOG.info("Password reset completed successfully for user ${resetToken.user.username}")
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
            LOG.debug("Token validation failed - token $token expired for username: ${resetToken.user.username}")
            passwordResetTokenPersistence.deleteByToken(resetToken.token)
            return false
        }

        LOG.debug("Token validation successful for username: ${resetToken.user.username}")
        return true
    }
}
