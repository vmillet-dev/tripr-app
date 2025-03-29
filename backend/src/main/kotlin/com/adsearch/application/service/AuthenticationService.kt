package com.adsearch.application.service

import com.adsearch.application.port.AuthenticationUseCase
import com.adsearch.domain.exception.AuthenticationException
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserRepositoryPort
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Service for authentication operations
 */
@Service
class AuthenticationService(
    private val userRepository: UserRepositoryPort,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    private val passwordEncoder: PasswordEncoder,
    
    @Value("\${jwt.refresh-token.cookie-name}")
    private val refreshTokenCookieName: String,
    
    @Value("\${jwt.refresh-token.expiration}")
    private val refreshTokenExpiration: Long
) : AuthenticationUseCase {
    
    /**
     * Authenticate a user with username and password
     */
    override suspend fun authenticate(authRequest: AuthRequest): AuthResponse {
        val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
        
        val user = userRepository.findByUsername(authRequest.username)
            ?: throw UserNotFoundException("User not found with username: ${authRequest.username}")

        logger.debug("Found user for authentication: {} with ID: {}", user, user.id)
        
        if (!passwordEncoder.matches(authRequest.password, user.password)) {
            throw AuthenticationException("Invalid password")
        }
        
        val accessToken = jwtService.generateToken(user)
        
        // Save the user to ensure it's available for refresh token
        userRepository.save(user)
        
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
        val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
        
        val refreshToken = extractRefreshTokenFromCookies(request)
        logger.debug("Extracted refresh token: $refreshToken")
        
        if (refreshToken == null) {
            logger.error("Refresh token is missing in cookies")
            throw InvalidTokenException("Refresh token is missing")
        }
        
        val storedToken = refreshTokenService.findByToken(refreshToken)
        logger.debug("Found stored token: {}", storedToken)
        
        if (storedToken == null) {
            logger.error("Refresh token not found in database: $refreshToken")
            throw InvalidTokenException("Refresh token not found in database")
        }
        
        val verifiedToken = refreshTokenService.verifyExpiration(storedToken)
        logger.debug("Verified token: {}", verifiedToken)
        
        // Log all users in the repository for debugging
        logger.debug("All users in repository:")
        val allUsers = userRepository.findAll()
        allUsers.forEach { logger.debug("User: {} with ID: {}", it, it.id) }
        
        val user = userRepository.findById(verifiedToken.userId)
        logger.debug("Found user: {} with ID: {}", user, verifiedToken.userId)
        
        if (user == null) {
            logger.error("User not found for refresh token with user ID: ${verifiedToken.userId}")
            throw UserNotFoundException("User not found for refresh token")
        }
        
        val accessToken = jwtService.generateToken(user)
        
        // Create a new refresh token and update the cookie
        val newRefreshToken = refreshTokenService.createRefreshToken(user)
        logger.debug("Created new refresh token: {}", newRefreshToken)
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
        return userRepository.findById(userId)
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
        val existingUser = userRepository.findByUsername(authRequest.username)
        if (existingUser != null) {
            throw AuthenticationException("Username already exists")
        }
        
        val user = User(
            username = authRequest.username,
            password = passwordEncoder.encode(authRequest.password),
            roles = mutableListOf("USER")
        )
        
        userRepository.save(user)
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
