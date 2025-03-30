package com.adsearch.infrastructure.web.controller

import com.adsearch.application.port.AuthenticationUseCase
import com.adsearch.application.service.AuthenticationService
import com.adsearch.application.service.RefreshTokenService
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.User
import com.adsearch.infrastructure.web.dto.AuthRequestDto
import com.adsearch.infrastructure.web.dto.AuthResponseDto
import com.adsearch.infrastructure.web.dto.RegisterRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for user authentication and token management")
class AuthController(
    private val authenticationUseCase: AuthenticationUseCase,
    private val authenticationService: AuthenticationService,
    private val refreshTokenService: RefreshTokenService
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
        
        val authResponse = authenticationUseCase.authenticate(authRequest)
        
        // Create a refresh token and add it to the response as an HTTP-only cookie
        val user = User(
            username = authResponse.username,
            password = "", // We don't need the password here
            roles = authResponse.roles.toMutableList()
        )
        
        val refreshToken = refreshTokenService.createRefreshToken(user)
        authenticationService.addRefreshTokenCookie(response, refreshToken.token)
        
        return ResponseEntity.ok(
            AuthResponseDto(
                accessToken = authResponse.accessToken,
                username = authResponse.username,
                roles = authResponse.roles
            )
        )
    }
    
    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Uses the refresh token cookie to generate a new access token")
    suspend fun refreshToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<AuthResponseDto> {
        val authResponse = authenticationUseCase.refreshToken(request, response)
        
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
        authenticationUseCase.logout(request, response)
        
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
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
}
