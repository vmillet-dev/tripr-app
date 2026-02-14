package com.adsearch.infrastructure.adapter.`in`.rest

import com.adsearch.domain.model.auth.AuthResponse
import com.adsearch.domain.model.command.LoginUserCommand
import com.adsearch.domain.model.command.RegisterUserCommand
import com.adsearch.domain.port.`in`.*
import com.adsearch.infrastructure.adapter.`in`.rest.dto.*
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
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Controller for authentication endpoints
 */
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API for user authentication and token management")
@PreAuthorize("permitAll()")
class AuthController(
    private val loginUserUseCase: LoginUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val passwordResetUseCase: PasswordResetUseCase,
    @param:Value($$"${jwt.refresh-token.cookie-name}") private val cookieName: String,
    @param:Value($$"${jwt.refresh-token.expiration}") private val cookieMaxAge: Int
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * Login endpoint
     */
    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user with username and password, returns JWT token and sets refresh token cookie"
    )
    fun login(
        @Valid @RequestBody request: AuthRequestDto,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDto> {

        val authResponse: AuthResponse = loginUserUseCase.login(
            LoginUserCommand(
                request.username,
                request.password
            )
        )

        authResponse.refreshToken?.let { token ->
            Cookie(cookieName, token).apply {
                maxAge = cookieMaxAge
                path = "/"
                isHttpOnly = true
                //TODO enable only in prod mode `secure = true`
                response.addCookie(this)
            }
        }

        return ResponseEntity.ok(AuthResponseDto(authResponse.accessToken))
    }

    /**
     * Register endpoint
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registers a new user with username, password, and email")
    fun register(@Valid @RequestBody request: RegisterRequestDto): ResponseEntity<Map<String, String>> {
        createUserUseCase.createUser(
            RegisterUserCommand(
                request.username,
                request.email,
                request.password
            )
        )

        return ResponseEntity.ok(mapOf("message" to "UserEntity registered successfully"))
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Uses the refresh token cookie to generate a new access token"
    )
    fun refreshToken(request: HttpServletRequest): ResponseEntity<AuthResponseDto> {
        val cookies: String? = request.cookies?.find { it.name == cookieName }?.value
        val authResponse = refreshTokenUseCase.refreshAccessToken(cookies)

        return ResponseEntity.ok(AuthResponseDto(authResponse.accessToken))
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Logout user",
        description = "Invalidates the refresh token and clears the refresh token cookie"
    )
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Map<String, String>> {
        val cookies: String? = request.cookies?.find { it.name == cookieName }?.value

        logoutUserUseCase.logout(cookies)

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
    @Operation(
        summary = "Request password reset",
        description = "Sends a password reset email to the user if the username exists"
    )
    fun requestPasswordReset(
        @Valid @RequestBody request: PasswordResetRequestDto
    ): ResponseEntity<Map<String, String>> {
        LOG.info("Received password reset request for username: ${request.username}")

        passwordResetUseCase.requestPasswordReset(request.username)

        return ResponseEntity.ok(mapOf("message" to "If the username exists, a password reset email has been sent"))
    }

    /**
     * Reset a password using a token
     */
    @PostMapping("/password/reset")
    @Operation(summary = "Reset password", description = "Resets a user's password using a valid token")
    fun resetPassword(@Valid @RequestBody request: PasswordResetDto): ResponseEntity<Map<String, String>> {
        LOG.info("Received password reset with token")

        passwordResetUseCase.resetPassword(request.token, request.newPassword)

        return ResponseEntity.ok(mapOf("message" to "Password has been reset successfully"))
    }

    /**
     * Validate a password reset token
     */
    @GetMapping("/password/validate-token")
    @Operation(summary = "Validate token", description = "Checks if a password reset token is valid and not expired")
    fun validateToken(@RequestParam token: String): ResponseEntity<Map<String, Boolean>> {
        LOG.info("Validating password reset token")

        val isValid = passwordResetUseCase.validateToken(token)

        return ResponseEntity.ok(mapOf("valid" to isValid))
    }
}
