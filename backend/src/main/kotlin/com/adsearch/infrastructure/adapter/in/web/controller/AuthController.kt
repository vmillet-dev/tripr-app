package com.adsearch.infrastructure.adapter.`in`.web.controller

import com.adsearch.application.AuthenticationUseCase
import com.adsearch.domain.model.AuthResponseDom
import com.adsearch.domain.model.UserDom
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
    fun login(@Valid @RequestBody request: AuthRequestDto, response: HttpServletResponse): ResponseEntity<AuthResponseDto> {
        val authResponse: AuthResponseDom = authenticationUseCase.login(request.username, request.password)

        authResponse.refreshToken?.let { token ->
            Cookie(cookieName, token).apply {
                maxAge = cookieMaxAge
                path = "/"
                isHttpOnly = true
                //TODO enable only in prod mode `secure = true`
                response.addCookie(this)
            }
        }

        return ResponseEntity.ok(
            AuthResponseDto(
                accessToken = authResponse.accessToken,
                username = authResponse.user.username,
                roles = authResponse.user.roles
            )
        )
    }

    /**
     * Register endpoint
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registers a new user with username, password, and email")
    fun register(@Valid @RequestBody request: RegisterRequestDto): ResponseEntity<Map<String, String>> {
        val user = UserDom(
            username = request.username,
            email = request.email,
            password = request.password
        )

        authenticationUseCase.register(user)

        return ResponseEntity.ok(mapOf("message" to "User registered successfully"))
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Uses the refresh token cookie to generate a new access token")
    fun refreshToken(request: HttpServletRequest): ResponseEntity<AuthResponseDto> {
        val cookies: String? = request.cookies?.find { it.name == cookieName }?.value
        val authResponse = authenticationUseCase.refreshAccessToken(cookies)

        return ResponseEntity.ok(
            AuthResponseDto(
                accessToken = authResponse.accessToken,
                username = authResponse.user.username,
                roles = authResponse.user.roles
            )
        )
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates the refresh token and clears the refresh token cookie")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Map<String, String>> {
        val cookies: String? = request.cookies?.find { it.name == cookieName }?.value

        authenticationUseCase.logout(cookies)

        Cookie(cookieName, "").apply {
            maxAge = 0
            path = "/"
            isHttpOnly = true
            //TODO enable only in prod mode `secure = true`
            response.addCookie(this)
        }

        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }

    /**
     * Request a password reset
     */
    @PostMapping("/password/reset-request")
    @Operation(summary = "Request password reset", description = "Sends a password reset email to the user if the username exists")
    fun requestPasswordReset(
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
    fun resetPassword(@Valid @RequestBody request: PasswordResetDto): ResponseEntity<Map<String, String>> {
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
    fun validateToken(@RequestParam token: String): ResponseEntity<Map<String, Boolean>> {
        LOG.info("Validating password reset token")

        val isValid = authenticationUseCase.validateToken(token)

        return ResponseEntity.ok(mapOf(
            "valid" to isValid
        ))
    }
}
