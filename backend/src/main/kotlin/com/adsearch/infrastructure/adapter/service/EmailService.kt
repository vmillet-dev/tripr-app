package com.adsearch.infrastructure.adapter.service

import com.adsearch.domain.port.service.EmailServicePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

/**
 * Implementation of EmailServicePort using Spring Mail
 * This is a secondary adapter in the hexagonal architecture
 */
@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine
) : EmailServicePort {
    
    private val logger = LoggerFactory.getLogger(EmailService::class.java)
    
    override suspend fun sendPasswordResetEmail(to: String, resetLink: String) {
        withContext(Dispatchers.IO) {
            try {
                val message = mailSender.createMimeMessage()
                val helper = MimeMessageHelper(message, true, "UTF-8")
                
                helper.setTo(to)
                helper.setSubject("Password Reset Request")
                
                val context = Context()
                context.setVariable("resetLink", resetLink)
                
                val htmlContent = templateEngine.process("email/password-reset-email", context)
                helper.setText(htmlContent, true)
                
                mailSender.send(message)
                logger.info("Password reset email sent to: $to")
            } catch (e: Exception) {
                logger.error("Failed to send password reset email to: $to", e)
                throw e
            }
        }
    }
}
