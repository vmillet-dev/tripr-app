package com.adsearch.infrastructure.adapter.out.email

import com.adsearch.infrastructure.service.EmailService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Email Service Adapter Tests")
class EmailServiceAdapterTest {

    private lateinit var emailServiceAdapter: EmailServiceAdapter
    private val emailService = mockk<EmailService>()

    @BeforeEach
    fun setUp() {
        emailServiceAdapter = EmailServiceAdapter(emailService)
    }

    @Test
    @DisplayName("Should send password reset email when requested")
    fun shouldSendPasswordResetEmailWhenRequested() {
        // Given
        val emailAddress = "user@example.com"
        val resetToken = "reset-token-123"

        every { emailService.sendPasswordResetEmail(emailAddress, resetToken) } returns Unit

        // When
        emailServiceAdapter.sendPasswordResetEmail(emailAddress, resetToken)

        // Then
        verify(exactly = 1) { emailService.sendPasswordResetEmail(emailAddress, resetToken) }
    }

    @Test
    @DisplayName("Should delegate password reset email call to email service")
    fun shouldDelegatePasswordResetEmailCallToEmailService() {
        // Given
        val recipientEmail = "delegate@example.com"
        val passwordResetToken = "delegate-token-456"

        every { emailService.sendPasswordResetEmail(recipientEmail, passwordResetToken) } returns Unit

        // When
        emailServiceAdapter.sendPasswordResetEmail(recipientEmail, passwordResetToken)

        // Then
        verify(exactly = 1) { emailService.sendPasswordResetEmail(recipientEmail, passwordResetToken) }
    }

    @Test
    @DisplayName("Should handle empty email address correctly")
    fun shouldHandleEmptyEmailAddressCorrectly() {
        // Given
        val emptyEmail = ""
        val token = "token-for-empty-email"

        every { emailService.sendPasswordResetEmail(emptyEmail, token) } returns Unit

        // When
        emailServiceAdapter.sendPasswordResetEmail(emptyEmail, token)

        // Then
        verify(exactly = 1) { emailService.sendPasswordResetEmail(emptyEmail, token) }
    }

    @Test
    @DisplayName("Should handle empty token correctly")
    fun shouldHandleEmptyTokenCorrectly() {
        // Given
        val email = "test@example.com"
        val emptyToken = ""

        every { emailService.sendPasswordResetEmail(email, emptyToken) } returns Unit

        // When
        emailServiceAdapter.sendPasswordResetEmail(email, emptyToken)

        // Then
        verify(exactly = 1) { emailService.sendPasswordResetEmail(email, emptyToken) }
    }

    @Test
    @DisplayName("Should handle multiple email sending requests independently")
    fun shouldHandleMultipleEmailSendingRequestsIndependently() {
        // Given
        val email1 = "user1@example.com"
        val token1 = "token1"
        val email2 = "user2@example.com"
        val token2 = "token2"
        val email3 = "user3@example.com"
        val token3 = "token3"

        every { emailService.sendPasswordResetEmail(email1, token1) } returns Unit
        every { emailService.sendPasswordResetEmail(email2, token2) } returns Unit
        every { emailService.sendPasswordResetEmail(email3, token3) } returns Unit

        // When
        emailServiceAdapter.sendPasswordResetEmail(email1, token1)
        emailServiceAdapter.sendPasswordResetEmail(email2, token2)
        emailServiceAdapter.sendPasswordResetEmail(email3, token3)

        // Then
        verify(exactly = 1) { emailService.sendPasswordResetEmail(email1, token1) }
        verify(exactly = 1) { emailService.sendPasswordResetEmail(email2, token2) }
        verify(exactly = 1) { emailService.sendPasswordResetEmail(email3, token3) }
    }

    @Test
    @DisplayName("Should handle long email addresses correctly")
    fun shouldHandleLongEmailAddressesCorrectly() {
        // Given
        val longEmail = "very.long.email.address.with.many.dots.and.characters@very.long.domain.name.example.com"
        val token = "token-for-long-email"

        every { emailService.sendPasswordResetEmail(longEmail, token) } returns Unit

        // When
        emailServiceAdapter.sendPasswordResetEmail(longEmail, token)

        // Then
        verify(exactly = 1) { emailService.sendPasswordResetEmail(longEmail, token) }
    }

    @Test
    @DisplayName("Should handle long tokens correctly")
    fun shouldHandleLongTokensCorrectly() {
        // Given
        val email = "user@example.com"
        val longToken = "very-long-token-with-many-characters-and-numbers-123456789-abcdefghijklmnopqrstuvwxyz-ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        every { emailService.sendPasswordResetEmail(email, longToken) } returns Unit

        // When
        emailServiceAdapter.sendPasswordResetEmail(email, longToken)

        // Then
        verify(exactly = 1) { emailService.sendPasswordResetEmail(email, longToken) }
    }

    @Test
    @DisplayName("Should handle special characters in email and token correctly")
    fun shouldHandleSpecialCharactersInEmailAndTokenCorrectly() {
        // Given
        val emailWithSpecialChars = "user+test@example-domain.co.uk"
        val tokenWithSpecialChars = "token-with-special-chars_123!@#$%"

        every { emailService.sendPasswordResetEmail(emailWithSpecialChars, tokenWithSpecialChars) } returns Unit

        // When
        emailServiceAdapter.sendPasswordResetEmail(emailWithSpecialChars, tokenWithSpecialChars)

        // Then
        verify(exactly = 1) { emailService.sendPasswordResetEmail(emailWithSpecialChars, tokenWithSpecialChars) }
    }

    @Test
    @DisplayName("Should maintain consistent behavior across repeated calls with same parameters")
    fun shouldMaintainConsistentBehaviorAcrossRepeatedCallsWithSameParameters() {
        // Given
        val email = "consistent@example.com"
        val token = "consistent-token"

        every { emailService.sendPasswordResetEmail(email, token) } returns Unit

        // When
        emailServiceAdapter.sendPasswordResetEmail(email, token)
        emailServiceAdapter.sendPasswordResetEmail(email, token)
        emailServiceAdapter.sendPasswordResetEmail(email, token)

        // Then
        verify(exactly = 3) { emailService.sendPasswordResetEmail(email, token) }
    }
}
