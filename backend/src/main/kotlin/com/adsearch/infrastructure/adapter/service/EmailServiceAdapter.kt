package com.adsearch.infrastructure.adapter.service

import com.adsearch.domain.port.service.EmailServicePort
import com.adsearch.infrastructure.service.EmailService
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

/**
 * Adapter implementation for EmailServicePort that delegates to the original EmailService
 * This follows the adapter pattern in hexagonal architecture
 */
@Service
@Primary
class EmailServiceAdapter(private val emailService: EmailService) : EmailServicePort {
    
    override suspend fun sendPasswordResetEmail(to: String, resetLink: String) {
        emailService.sendPasswordResetEmail(to, resetLink)
    }
}
