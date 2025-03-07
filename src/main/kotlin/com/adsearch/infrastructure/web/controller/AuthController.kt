package com.adsearch.infrastructure.web.controller

import com.adsearch.application.port.AuthenticationUseCase
import com.adsearch.application.service.AuthenticationService
import com.adsearch.application.service.RefreshTokenService
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.User
import com.adsearch.infrastructure.web.dto.AuthRequestDto
import com.adsearch.infrastructure.web.dto.AuthResponseDto
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
class AuthController(
    private val authenticationUseCase: AuthenticationUseCase,
    private val authenticationService: AuthenticationService,
    private val refreshTokenService: RefreshTokenService
) {
    
    /**
     * Login endpoint
     */
    @PostMapping("/login")
    suspend fun login(
        @Valid @RequestBody request: AuthRequestDto,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDto> {
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
    suspend fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDto> {
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
    suspend fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<Map<String, String>> {
        authenticationUseCase.logout(request, response)
        
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
}
