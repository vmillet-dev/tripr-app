package com.adsearch.infrastructure.adapter.`in`.web.controller

import com.adsearch.application.usecase.AuthenticationUseCase
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.infrastructure.adapter.`in`.web.dto.AuthRequestDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.AuthResponseDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.PasswordResetDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.PasswordResetRequestDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.RegisterRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for authentication endpoints
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API for user authentication and token management")
class AuthController(
    private val authenticationUseCase: AuthenticationUseCase,
    @Value("\${jwt.refresh-token.cookie-name}") private val cookieName: String,
    @Value("\${jwt.refresh-token.expiration}") private val cookieMaxAge: Int
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user with username and password, returns JWT token and sets refresh token cookie")
    suspend fun login(@Valid @RequestBody request: AuthRequestDto, response: HttpServletResponse, environment: Environment): ResponseEntity<AuthResponseDto> {
        val authResponse: AuthResponse = authenticationUseCase.login(request.username, request.password)

        val cookie = Cookie(cookieName, authResponse.refreshToken!!.token)
        cookie.maxAge =  cookieMaxAge
        cookie.path = "/"
        cookie.isHttpOnly = true

        if (environment.activeProfiles.isEmpty()) {
            cookie.secure = true
        }

        response.addCookie(cookie)

        return ResponseEntity.ok(
            AuthResponseDto(
                accessToken = authResponse.accessToken,
                username = authResponse.username,
                roles = authResponse.roles
            )
        )
    }

    /**
     * Register endpoint
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registers a new user with username, password, and email")
    suspend fun register(@Valid @RequestBody request: RegisterRequestDto): ResponseEntity<Map<String, String>> {
        return try {
            val authRequest = AuthRequest(
                username = request.username,
                password = request.password
            )

            authenticationUseCase.register(authRequest, request.email)

            ResponseEntity.ok(mapOf("message" to "User registered successfully"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("message" to (e.message ?: "Registration failed")))
        }
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Uses the refresh token cookie to generate a new access token")
    suspend fun refreshToken(request: HttpServletRequest): ResponseEntity<AuthResponseDto> {
        val cookies: String? = request.cookies?.find { it.name == cookieName }?.value
        val authResponse = authenticationUseCase.refreshAccessToken(cookies)

        return ResponseEntity.ok(
            AuthResponseDto(
                accessToken = authResponse.accessToken,
                username = authResponse.username,
                roles = authResponse.roles
            )
        )
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates the refresh token and clears the refresh token cookie")
    suspend fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        environment: Environment
    ): ResponseEntity<Map<String, String>> {
        val cookies: String? = request.cookies?.find { it.name == cookieName }?.value

        authenticationUseCase.logout(cookies)

        val cookie = Cookie(cookieName, "")
        cookie.maxAge = 0
        cookie.path = "/"
        cookie.isHttpOnly = true

        if (environment.activeProfiles.isEmpty()) {
            cookie.secure = true
        }
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }

    /**
     * Request a password reset
     */
    @PostMapping("/password/reset-request")
    @Operation(summary = "Request password reset", description = "Sends a password reset email to the user if the username exists")
    suspend fun requestPasswordReset(
        @Valid @RequestBody request: PasswordResetRequestDto
    ): ResponseEntity<Map<String, String>> {
        LOG.info("Received password reset request for username: ${request.username}")

        authenticationUseCase.requestPasswordReset(request.username)

        return ResponseEntity.ok(mapOf(
            "message" to "If the username exists, a password reset email has been sent"
        ))
    }

    /**
     * Reset a password using a token
     */
    @PostMapping("/password/reset")
    @Operation(summary = "Reset password", description = "Resets a user's password using a valid token")
    suspend fun resetPassword(@Valid @RequestBody request: PasswordResetDto): ResponseEntity<Map<String, String>> {
        LOG.info("Received password reset with token")

        authenticationUseCase.resetPassword(request.token, request.newPassword)

        return ResponseEntity.ok(mapOf(
            "message" to "Password has been reset successfully"
        ))
    }

    /**
     * Validate a password reset token
     */
    @GetMapping("/password/validate-token")
    @Operation(summary = "Validate token", description = "Checks if a password reset token is valid and not expired")
    suspend fun validateToken(@RequestParam token: String): ResponseEntity<Map<String, Boolean>> {
        LOG.info("Validating password reset token")

        val isValid = authenticationUseCase.validateToken(token)

        return ResponseEntity.ok(mapOf(
            "valid" to isValid
        ))
    }
}
