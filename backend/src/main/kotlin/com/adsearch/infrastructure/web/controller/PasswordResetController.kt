package com.adsearch.infrastructure.web.controller

import com.adsearch.application.port.PasswordResetUseCase
import com.adsearch.infrastructure.web.dto.PasswordResetDto
import com.adsearch.infrastructure.web.dto.PasswordResetRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for password reset operations
 */
@RestController
@RequestMapping("/api/auth/password")
@Tag(name = "Password Reset", description = "API for password reset operations")
class PasswordResetController(private val passwordResetUseCase: PasswordResetUseCase) {
    
    private val logger = LoggerFactory.getLogger(PasswordResetController::class.java)
    
    /**
     * Request a password reset
     */
    @PostMapping("/reset-request")
    @Operation(summary = "Request password reset", description = "Sends a password reset email to the user if the username exists")
    suspend fun requestPasswordReset(
        @Valid @RequestBody request: PasswordResetRequestDto
    ): ResponseEntity<Map<String, String>> {
        logger.info("Received password reset request for username: ${request.username}")
        
        passwordResetUseCase.requestPasswordReset(request.username)

        return ResponseEntity.ok(mapOf(
            "message" to "If the username exists, a password reset email has been sent"
        ))
    }
    
    /**
     * Reset a password using a token
     */
    @PostMapping("/reset")
    @Operation(summary = "Reset password", description = "Resets a user's password using a valid token")
    suspend fun resetPassword(@Valid @RequestBody request: PasswordResetDto): ResponseEntity<Map<String, String>> {
        logger.info("Received password reset with token")
        
        passwordResetUseCase.resetPassword(request.token, request.newPassword)

        return ResponseEntity.ok(mapOf(
            "message" to "Password has been reset successfully"
        ))
    }
    
    /**
     * Validate a password reset token
     */
    @GetMapping("/validate-token")
    @Operation(summary = "Validate token", description = "Checks if a password reset token is valid and not expired")
    suspend fun validateToken(@RequestParam token: String): ResponseEntity<Map<String, Boolean>> {
        logger.info("Validating password reset token")
        
        val isValid = passwordResetUseCase.validateToken(token)

        return ResponseEntity.ok(mapOf(
            "valid" to isValid
        ))
    }
}
