package com.adsearch.application.service

import com.adsearch.application.usecase.AuthenticationUseCase
import com.adsearch.common.exception.InvalidCredentialsException
import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.UserAlreadyExistsException
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserPersistencePort
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Service for authentication operations
 */
@Service
class AuthenticationService(
    private val userPersistencePort: UserPersistencePort,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    private val passwordEncoder: PasswordEncoder,

    @Value("\${jwt.refresh-token.cookie-name}")
    private val refreshTokenCookieName: String,

    @Value("\${jwt.refresh-token.expiration}")
    private val refreshTokenExpiration: Long
) : AuthenticationUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Authenticate a user with username and password
     */
    override suspend fun authenticate(authRequest: AuthRequest): AuthResponse {
        val user = userPersistencePort.findByUsername(authRequest.username) ?: throw InvalidCredentialsException()

        LOG.debug("Found user for authentication: {} with ID: {}", user.username, user.id)

        if (!passwordEncoder.matches(authRequest.password, user.password)) {
            throw InvalidCredentialsException()
        }

        val accessToken = jwtService.generateToken(user)

        // Save the user to ensure it's available for refresh token
        userPersistencePort.save(user)

        return AuthResponse(
            accessToken = accessToken,
            username = user.username,
            roles = user.roles
        )
    }

    /**
     * Refresh an access token using a refresh token
     */
    override suspend fun refreshToken(request: HttpServletRequest, response: HttpServletResponse): AuthResponse {
        val refreshToken = extractRefreshTokenFromCookies(request)
        LOG.debug("Extracted refresh token: $refreshToken")

        if (refreshToken == null) {
            LOG.error("Refresh token is missing in cookies")
            throw InvalidTokenException(message = "Refresh token is missing")
        }

        val storedToken = refreshTokenService.findByToken(refreshToken)
        LOG.debug("Found stored token: {}", storedToken)

        if (storedToken == null) {
            LOG.error("Refresh token not found in database: $refreshToken")
            throw InvalidTokenException(message = "Refresh token not found in database")
        }

        val verifiedToken = refreshTokenService.verifyExpiration(storedToken)
        LOG.debug("Verified token: {}", verifiedToken)

        // Log all users in the repository for debugging
        LOG.debug("All users in repository:")
        val allUsers = userPersistencePort.findAll()
        allUsers.forEach { LOG.debug("User: {} with ID: {}", it, it.id) }

        val user = userPersistencePort.findById(verifiedToken.userId)
        LOG.debug("Found user: {} with ID: {}", user, verifiedToken.userId)

        if (user == null) {
            LOG.error("User not found for refresh token with user ID: ${verifiedToken.userId}")
            throw InvalidTokenException(message = "User not found for refresh token")
        }

        val accessToken = jwtService.generateToken(user)

        // Create a new refresh token and update the cookie
        val newRefreshToken = refreshTokenService.createRefreshToken(user)
        LOG.debug("Created new refresh token: {}", newRefreshToken)
        addRefreshTokenCookie(response, newRefreshToken.token)

        return AuthResponse(
            accessToken = accessToken,
            username = user.username,
            roles = user.roles
        )
    }

    /**
     * Logout a user by invalidating their refresh tokens
     */
    override suspend fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        val refreshToken = extractRefreshTokenFromCookies(request)

        if (refreshToken != null) {
            val storedToken = refreshTokenService.findByToken(refreshToken)

            if (storedToken != null) {
                refreshTokenService.deleteByUserId(storedToken.userId)
            }
        }

        // Clear the refresh token cookie
        val cookie = Cookie(refreshTokenCookieName, "")
        cookie.maxAge = 0
        cookie.path = "/"
        cookie.isHttpOnly = true
        cookie.secure = true
        response.addCookie(cookie)
    }

    /**
     * Get the current authenticated user
     */
    override suspend fun getCurrentUser(request: HttpServletRequest): User? {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null
        }

        val token = authHeader.substring(7)

        if (!jwtService.validateToken(token)) {
            return null
        }

        val userId = jwtService.getUserIdFromToken(token)
        return userPersistencePort.findById(userId)
    }

    /**
     * Validate an access token
     */
    override fun validateToken(token: String): Boolean {
        return jwtService.validateToken(token)
    }

    /**
     * Register a new user
     */
    override suspend fun register(authRequest: AuthRequest, email: String) {
        val existingUser = userPersistencePort.findByUsername(authRequest.username)
        if (existingUser != null) {
            throw UserAlreadyExistsException("Username already exists")
        }

        val user = User(
            username = authRequest.username,
            password = passwordEncoder.encode(authRequest.password),
            roles = mutableListOf("USER")
        )

        userPersistencePort.save(user)
    }

    /**
     * Extract refresh token from cookies
     */
    private fun extractRefreshTokenFromCookies(request: HttpServletRequest): String? {
        val cookies = request.cookies ?: return null

        return cookies.find { it.name == refreshTokenCookieName }?.value
    }

    /**
     * Add refresh token cookie to response
     */
    fun addRefreshTokenCookie(response: HttpServletResponse, refreshToken: String) {
        val cookie = Cookie(refreshTokenCookieName, refreshToken)
        cookie.maxAge = (refreshTokenExpiration / 1000).toInt() // Convert milliseconds to seconds
        cookie.path = "/"
        cookie.isHttpOnly = true
        cookie.secure = true
        response.addCookie(cookie)
    }
}
