package com.adsearch.infrastructure.adapter.out.email

import com.adsearch.domain.port.api.EmailServicePort
import com.adsearch.infrastructure.adapter.out.email.service.EmailService
import org.springframework.stereotype.Component

/**
 * Implementation of EmailServicePort using Spring Mail
 */
@Component
class EmailServiceAdapter(
    private val emailService: EmailService,
) : EmailServicePort {

    override fun sendPasswordResetEmail(to: String, token: String) {
        emailService.sendPasswordResetEmail(to, token)
    }
}
