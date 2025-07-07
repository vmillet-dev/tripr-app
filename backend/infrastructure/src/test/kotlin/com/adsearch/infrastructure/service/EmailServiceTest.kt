package com.adsearch.infrastructure.service

import com.adsearch.infrastructure.exception.MailSendException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.test.util.ReflectionTestUtils
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("Email Service Tests")
class EmailServiceTest {

    private lateinit var emailService: EmailService
    private val mailSender = mockk<JavaMailSender>()
    private val templateEngine = mockk<TemplateEngine>()
    private val mimeMessage = mockk<MimeMessage>(relaxed = true)
    
    private val testBaseUrl = "http://localhost:3000"
    private val testFromEmail = "noreply@test.com"

    @BeforeEach
    fun setUp() {
        emailService = EmailService(mailSender, templateEngine, testBaseUrl, testFromEmail)
    }

    @Test
    @DisplayName("Should send password reset email successfully when valid parameters are provided")
    fun shouldSendPasswordResetEmailSuccessfullyWhenValidParametersAreProvided() {
        // Given
        val toEmail = "user@example.com"
        val token = "reset-token-123"
        val expectedHtmlContent = "<html><body>Reset your password</body></html>"
        val contextSlot = slot<Context>()

        every { mailSender.createMimeMessage() } returns mimeMessage
        every { templateEngine.process("email/password-reset-email", capture(contextSlot)) } returns expectedHtmlContent
        every { mailSender.send(mimeMessage) } returns Unit

        // When
        emailService.sendPasswordResetEmail(toEmail, token)

        // Then
        verify(exactly = 1) { mailSender.createMimeMessage() }
        verify(exactly = 1) { templateEngine.process("email/password-reset-email", any<Context>()) }
        verify(exactly = 1) { mailSender.send(mimeMessage) }
        
        val capturedContext = contextSlot.captured
        val resetLink = capturedContext.getVariable("resetLink")
        assertEquals("$testBaseUrl/password-reset?token=$token", resetLink)
    }

    @Test
    @DisplayName("Should throw MailSendException when JavaMailSender throws exception")
    fun shouldThrowMailSendExceptionWhenJavaMailSenderThrowsException() {
        // Given
        val toEmail = "user@example.com"
        val token = "reset-token-123"
        val expectedHtmlContent = "<html><body>Reset your password</body></html>"

        every { mailSender.createMimeMessage() } returns mimeMessage
        every { templateEngine.process("email/password-reset-email", any<Context>()) } returns expectedHtmlContent
        every { mailSender.send(mimeMessage) } throws RuntimeException("Mail server error")

        // When & Then
        val exception = assertThrows<MailSendException> {
            emailService.sendPasswordResetEmail(toEmail, token)
        }
        
        assertTrue(exception.message!!.contains("Failed to send password reset email to: $toEmail"))
        assertEquals(RuntimeException::class, exception.cause!!::class)
    }

    @Test
    @DisplayName("Should throw MailSendException when TemplateEngine throws exception")
    fun shouldThrowMailSendExceptionWhenTemplateEngineThrowsException() {
        // Given
        val toEmail = "user@example.com"
        val token = "reset-token-123"

        every { mailSender.createMimeMessage() } returns mimeMessage
        every { templateEngine.process("email/password-reset-email", any<Context>()) } throws RuntimeException("Template processing error")

        // When & Then
        val exception = assertThrows<MailSendException> {
            emailService.sendPasswordResetEmail(toEmail, token)
        }
        
        assertTrue(exception.message!!.contains("Failed to send password reset email to: $toEmail"))
        assertEquals(RuntimeException::class, exception.cause!!::class)
    }

    @Test
    @DisplayName("Should create correct reset link with base URL and token")
    fun shouldCreateCorrectResetLinkWithBaseUrlAndToken() {
        // Given
        val toEmail = "test@example.com"
        val token = "unique-token-456"
        val expectedHtmlContent = "<html><body>Reset link created</body></html>"
        val contextSlot = slot<Context>()

        every { mailSender.createMimeMessage() } returns mimeMessage
        every { templateEngine.process("email/password-reset-email", capture(contextSlot)) } returns expectedHtmlContent
        every { mailSender.send(mimeMessage) } returns Unit

        // When
        emailService.sendPasswordResetEmail(toEmail, token)

        // Then
        val capturedContext = contextSlot.captured
        val resetLink = capturedContext.getVariable("resetLink") as String
        assertTrue(resetLink.contains(testBaseUrl))
        assertTrue(resetLink.contains(token))
        assertEquals("$testBaseUrl/password-reset?token=$token", resetLink)
    }

    @Test
    @DisplayName("Should process correct email template")
    fun shouldProcessCorrectEmailTemplate() {
        // Given
        val toEmail = "template@example.com"
        val token = "template-token"
        val expectedHtmlContent = "<html><body>Template processed</body></html>"

        every { mailSender.createMimeMessage() } returns mimeMessage
        every { templateEngine.process("email/password-reset-email", any<Context>()) } returns expectedHtmlContent
        every { mailSender.send(mimeMessage) } returns Unit

        // When
        emailService.sendPasswordResetEmail(toEmail, token)

        // Then
        verify(exactly = 1) { templateEngine.process("email/password-reset-email", any<Context>()) }
    }
}
