package com.adsearch.infrastructure.adapter.out.email

import com.adsearch.domain.port.api.EmailServicePort
import com.adsearch.infrastructure.adapter.out.email.service.EmailService
import org.springframework.stereotype.Service

/**
 * Implementation of EmailServicePort using Spring Mail
 */
@Service
class EmailServiceAdapter(
    private val emailService: EmailService,
) : EmailServicePort {

    override fun sendPasswordResetEmail(to: String, token: String) {
        emailService.sendPasswordResetEmail(to, token)
    }
}
