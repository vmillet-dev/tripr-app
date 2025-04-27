package com.adsearch.infrastructure.adapter.out.email.service

import com.adsearch.common.exception.technical.MailSendException
import com.adsearch.infrastructure.adapter.out.email.EmailServiceAdapter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    @Value("\${password-reset.base-url}") private val baseUrl: String
) {

    private val logger = LoggerFactory.getLogger(EmailServiceAdapter::class.java)

    fun sendPasswordResetEmail(to: String, token: String) {
        try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setTo(to)
            helper.setSubject("Password Reset Request")

            val context = Context()
            context.setVariable("resetLink", "$baseUrl?token=$token")

            val htmlContent = templateEngine.process("email/password-reset-email", context)
            helper.setText(htmlContent, true)

            mailSender.send(message)
            logger.info("Password reset email sent to: $to")
        } catch (e: Exception) {
            throw MailSendException("Failed to send password reset email to: $to", cause = e)
        }
    }
}
