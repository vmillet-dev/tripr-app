package com.adsearch.infrastructure.adapter.out.notification

import com.adsearch.domain.port.out.notification.EmailServicePort
import com.adsearch.domain.exception.MailSendException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

/**
 * Implementation of EmailServicePort using Spring Mail
 */
@Component
class EmailServiceAdapter(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    @param:Value($$"${password-reset.base-url}") private val baseUrl: String,
    @param:Value($$"${password-reset.from}") private val from: String
): EmailServicePort {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun sendPasswordResetEmail(to: String, token: String) {
        try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setTo(to)
            helper.setFrom(from)
            helper.setSubject("Password Reset Request")

            val context = Context()
            context.setVariable("resetLink", "$baseUrl/password-reset?token=$token")

            val htmlContent = templateEngine.process("email/password-reset-email", context)
            helper.setText(htmlContent, true)

            mailSender.send(message)
            LOG.info("Password reset email sent to: $to")
        } catch (e: Exception) {
            throw MailSendException("Failed to send password reset email to: $to", cause = e)
        }
    }
}
