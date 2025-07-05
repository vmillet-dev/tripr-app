package com.adsearch.application.impl

import com.adsearch.application.AuthenticationUseCase
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
        LOG.info("Login attempt for user $username")

        try {
            authenticationService.authenticate(username, password)
            LOG.debug("User $username authentication successful ")
        } catch (_: Exception) {
            throw InvalidCredentialsException("Authentication failed for user $username - invalid credentials provided")
        }

        val user: UserDom = authenticationService.loadAuthenticateUserByUsername(username)

        // Clean up existing refresh tokens
        refreshTokenPersistence.deleteByUserId(user.id)

        val refreshToken = RefreshTokenDom(
            userId = user.id,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusSeconds(authenticationService.getRefreshTokenExpiration())
        )
        refreshTokenPersistence.save(refreshToken)
        LOG.debug("Access token ${refreshToken.token} created for user $username with id: ${user.id}")

        val accessToken: String = jwtTokenService.createAccessToken(user.id.toString(), user.username, user.roles)
        LOG.debug("Access token $accessToken created for user $username with id: ${user.id}")

        LOG.info("Login successful for user $username")
        return AuthResponseDom(user, accessToken, refreshToken.token)
    }

    /**
     * Register a new user
     */
    override fun register(user: UserDom) {
        LOG.info("Registration attempt initiated for user ${user.username}")

        if (userPersistence.findByUsername(user.username) != null) {
            throw UsernameAlreadyExistsException("Registration failed - username ${user.username} already exists")
        }

        if (userPersistence.findByEmail(user.email) != null) {
            throw EmailAlreadyExistsException("Registration failed - email ${user.email} already exists")
        }


        val hashedPassword = authenticationService.generateHashedPassword(user.password)

        user.apply { password = hashedPassword }
        val userSaved: UserDom = userPersistence.save(user)

        LOG.info("User ${user.username} registration successful - new user id: ${userSaved.id}")
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
            refreshTokenPersistence.deleteById(refreshTokenDom.id)
            throw TokenExpiredException("Token refresh failed - refresh token expired or revoked for user id: ${refreshTokenDom.userId}")
        }

        val user: UserDom = userPersistence.findById(refreshTokenDom.userId)
            ?: throw UserNotFoundException("Token refresh failed - user not found with user id: ${refreshTokenDom.userId}")


        val authenticateUser: UserDom = authenticationService.loadAuthenticateUserByUsername(user.username)

        if (user.id != authenticateUser.id) {
            throw InvalidCredentialsException("Token refresh failed - user id mismatch during authentication")
        }

        val accessToken: String = jwtTokenService.createAccessToken(user.id.toString(), user.username, user.roles)
        LOG.debug("New access token $accessToken generated for user id: ${user.id}")

        LOG.info("Token refresh successful for user id: ${user.id}")
        return AuthResponseDom(authenticateUser, accessToken)
    }

    /**
     * Logout a user by invalidating their refresh tokens
     */
    override fun logout(refreshToken: String?) {
        LOG.debug("Logout attempt initiated with refresh token $refreshToken")

        refreshToken?.let { token ->
            refreshTokenPersistence.findByToken(token)?.let { storedToken ->
                refreshTokenPersistence.deleteByUserId(storedToken.userId)
                LOG.info("Logout successful - refresh tokens invalidated for user id: ${storedToken.userId}")
            } ?: LOG.warn("Logout attempted with invalid refresh token")
        } ?: LOG.debug("Logout attempted without refresh token")
    }

    /**
     * Request a password reset for a user
     */
    override fun requestPasswordReset(username: String) {
        LOG.info("Password reset request initiated for user $username")

        val user = userPersistence.findByUsername(username)
            ?: throw UserNotFoundException("Password reset request failed - user not found with username: $username")

        // Delete any existing tokens for this user
        passwordResetTokenPersistence.deleteByUserId(user.id)

        // Create a new token
        val resetToken = PasswordResetTokenDom(
            userId = user.id,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusSeconds(authenticationService.getPasswordResetTokenExpiration())
        )

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

        // Check if token is expired
        if (resetToken.isExpired()) {
            passwordResetTokenPersistence.deleteById(resetToken.id)
            throw TokenExpiredException("Password reset failed - token expired for user id: ${resetToken.userId}")
        }

        // Check if token has been used
        if (resetToken.used) {
            throw InvalidTokenException("Password reset failed - token already used for user id: ${resetToken.userId}")
        }

        // Find the user
        val user = userPersistence.findById(resetToken.userId)
            ?: throw UserNotFoundException("Password reset failed - user not found for token user id: ${resetToken.userId}")

        // Update the password
        user.apply {
            password = authenticationService.generateHashedPassword(newPassword)
        }
        userPersistence.save(user)

        // Delete all tokens for this user
        passwordResetTokenPersistence.deleteByUserId(user.id)

        LOG.info("Password reset completed successfully for user ${user.username}")
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

        // Check if token is expired
        if (resetToken.isExpired()) {
            LOG.debug("Token validation failed - token $token expired for user id: ${resetToken.userId}")
            passwordResetTokenPersistence.deleteById(resetToken.id)
            return false
        }

        // Check if token has been used
        if (resetToken.used) {
            LOG.debug("Token validation failed - token $token already used for user id: ${resetToken.userId}")
            return false
        }

        LOG.debug("Token validation successful for user id: ${resetToken.userId}")
        return true
    }
}
