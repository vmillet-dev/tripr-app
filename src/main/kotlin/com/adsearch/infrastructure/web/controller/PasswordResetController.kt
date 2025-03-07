package com.adsearch.infrastructure.web.controller

import com.adsearch.application.service.PasswordResetService
import com.adsearch.infrastructure.web.dto.PasswordResetDto
import com.adsearch.infrastructure.web.dto.PasswordResetRequestDto
import jakarta.validation.Valid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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
class PasswordResetController(
    private val passwordResetService: PasswordResetService,
    private val ioDispatcher: CoroutineDispatcher
) {
    
    private val logger = LoggerFactory.getLogger(PasswordResetController::class.java)
    
    /**
     * Request a password reset
     */
    @PostMapping("/reset-request")
    suspend fun requestPasswordReset(
        @Valid @RequestBody request: PasswordResetRequestDto
    ): ResponseEntity<Map<String, String>> {
        logger.info("Received password reset request for username: ${request.username}")
        
        return withContext(ioDispatcher) {
            passwordResetService.requestPasswordReset(request.username)
            
            ResponseEntity.ok(mapOf(
                "message" to "If the username exists, a password reset email has been sent"
            ))
        }
    }
    
    /**
     * Reset a password using a token
     */
    @PostMapping("/reset")
    suspend fun resetPassword(
        @Valid @RequestBody request: PasswordResetDto
    ): ResponseEntity<Map<String, String>> {
        logger.info("Received password reset with token")
        
        return withContext(ioDispatcher) {
            passwordResetService.resetPassword(request.token, request.newPassword)
            
            ResponseEntity.ok(mapOf(
                "message" to "Password has been reset successfully"
            ))
        }
    }
    
    /**
     * Validate a password reset token
     */
    @GetMapping("/validate-token")
    suspend fun validateToken(
        @RequestParam token: String
    ): ResponseEntity<Map<String, Boolean>> {
        logger.info("Validating password reset token")
        
        return withContext(ioDispatcher) {
            val isValid = passwordResetService.validateToken(token)
            
            ResponseEntity.ok(mapOf(
                "valid" to isValid
            ))
        }
    }
}
