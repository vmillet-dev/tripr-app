package com.adsearch.infrastructure.adapter.`in`.web.controller

import com.adsearch.application.usecase.AuthenticationUseCase
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.infrastructure.adapter.`in`.web.dto.AuthRequestDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.AuthResponseDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.RegisterRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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

    /**
     * Login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user with username and password, returns JWT token and sets refresh token cookie")
    suspend fun login(@Valid @RequestBody request: AuthRequestDto, response: HttpServletResponse): ResponseEntity<AuthResponseDto> {
        val authRequest = AuthRequest(
            username = request.username,
            password = request.password
        )

        val authResponse: AuthResponse = authenticationUseCase.login(authRequest)

        val cookie = Cookie(cookieName, authResponse.refreshToken!!.token)
        cookie.maxAge =  cookieMaxAge
        cookie.path = "/"
        cookie.isHttpOnly = true
        //TODO enable only in prod mode `cookie.secure = true`
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
        response: HttpServletResponse
    ): ResponseEntity<Map<String, String>> {
        val cookies: String? = request.cookies?.find { it.name == cookieName }?.value

        authenticationUseCase.logout(cookies)

        val cookie = Cookie(cookieName, "")
        cookie.maxAge = 0
        cookie.path = "/"
        cookie.isHttpOnly = true
        //TODO enable only in prod mode `cookie.secure = true`
        response.addCookie(cookie)
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
}
