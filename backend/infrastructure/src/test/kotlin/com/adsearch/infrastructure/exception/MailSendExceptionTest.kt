package com.adsearch.infrastructure.exception

import com.adsearch.domain.model.enum.HttpStatusEnum
import com.adsearch.domain.model.enum.TechnicalErrorCodeEnum
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("Mail Send Exception Tests")
class MailSendExceptionTest {

    @Test
    @DisplayName("Should create exception with default values when no parameters provided")
    fun shouldCreateExceptionWithDefaultValuesWhenNoParametersProvided() {
        // Given & When
        val exception = MailSendException()

        // Then
        assertNotNull(exception)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.defaultMessage, exception.message)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
        assertNull(exception.cause)
    }

    @Test
    @DisplayName("Should create exception with custom message when provided")
    fun shouldCreateExceptionWithCustomMessageWhenProvided() {
        // Given
        val customMessage = "Custom mail send error message"

        // When
        val exception = MailSendException(message = customMessage)

        // Then
        assertNotNull(exception)
        assertEquals(customMessage, exception.message)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
        assertNull(exception.cause)
    }

    @Test
    @DisplayName("Should create exception with custom error code when provided")
    fun shouldCreateExceptionWithCustomErrorCodeWhenProvided() {
        // Given
        val customErrorCode = "CUSTOM_MAIL_ERROR_001"

        // When
        val exception = MailSendException(errorCode = customErrorCode)

        // Then
        assertNotNull(exception)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.defaultMessage, exception.message)
        assertEquals(customErrorCode, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
        assertNull(exception.cause)
    }

    @Test
    @DisplayName("Should create exception with custom HTTP status when provided")
    fun shouldCreateExceptionWithCustomHttpStatusWhenProvided() {
        // Given
        val customHttpStatus = HttpStatusEnum.BAD_REQUEST

        // When
        val exception = MailSendException(httpStatusEnum = customHttpStatus)

        // Then
        assertNotNull(exception)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.defaultMessage, exception.message)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code, exception.errorCode)
        assertEquals(customHttpStatus, exception.httpStatusEnum)
        assertNull(exception.cause)
    }

    @Test
    @DisplayName("Should create exception with cause when provided")
    fun shouldCreateExceptionWithCauseWhenProvided() {
        // Given
        val cause = RuntimeException("SMTP server connection failed")

        // When
        val exception = MailSendException(cause = cause)

        // Then
        assertNotNull(exception)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.defaultMessage, exception.message)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
        assertEquals(cause, exception.cause)
    }

    @Test
    @DisplayName("Should create exception with all custom parameters when provided")
    fun shouldCreateExceptionWithAllCustomParametersWhenProvided() {
        // Given
        val customMessage = "Failed to send password reset email"
        val customErrorCode = "PASSWORD_RESET_MAIL_FAILED"
        val customHttpStatus = HttpStatusEnum.INTERNAL_SERVER_ERROR
        val cause = IllegalStateException("Mail service unavailable")

        // When
        val exception = MailSendException(
            message = customMessage,
            errorCode = customErrorCode,
            httpStatusEnum = customHttpStatus,
            cause = cause
        )

        // Then
        assertNotNull(exception)
        assertEquals(customMessage, exception.message)
        assertEquals(customErrorCode, exception.errorCode)
        assertEquals(customHttpStatus, exception.httpStatusEnum)
        assertEquals(cause, exception.cause)
    }

    @Test
    @DisplayName("Should handle empty message correctly")
    fun shouldHandleEmptyMessageCorrectly() {
        // Given
        val emptyMessage = ""

        // When
        val exception = MailSendException(message = emptyMessage)

        // Then
        assertNotNull(exception)
        assertEquals(emptyMessage, exception.message)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
    }

    @Test
    @DisplayName("Should handle empty error code correctly")
    fun shouldHandleEmptyErrorCodeCorrectly() {
        // Given
        val emptyErrorCode = ""

        // When
        val exception = MailSendException(errorCode = emptyErrorCode)

        // Then
        assertNotNull(exception)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.defaultMessage, exception.message)
        assertEquals(emptyErrorCode, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
    }

    @Test
    @DisplayName("Should maintain inheritance from BaseTechnicalException")
    fun shouldMaintainInheritanceFromBaseTechnicalException() {
        // Given & When
        val exception = MailSendException()

        // Then
        assertNotNull(exception)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.defaultMessage, exception.message)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code, exception.errorCode)
    }

    @Test
    @DisplayName("Should handle different HTTP status codes correctly")
    fun shouldHandleDifferentHttpStatusCodesCorrectly() {
        // Given
        val unauthorizedStatus = HttpStatusEnum.UNAUTHORIZED
        val notFoundStatus = HttpStatusEnum.BAD_REQUEST

        // When
        val unauthorizedException = MailSendException(httpStatusEnum = unauthorizedStatus)
        val notFoundException = MailSendException(httpStatusEnum = notFoundStatus)

        // Then
        assertEquals(unauthorizedStatus, unauthorizedException.httpStatusEnum)
        assertEquals(notFoundStatus, notFoundException.httpStatusEnum)
    }

    @Test
    @DisplayName("Should handle long custom messages correctly")
    fun shouldHandleLongCustomMessagesCorrectly() {
        // Given
        val longMessage = "This is a very long mail send exception message that describes in detail what went wrong when attempting to send an email through the mail service including SMTP configuration issues and network connectivity problems"

        // When
        val exception = MailSendException(message = longMessage)

        // Then
        assertNotNull(exception)
        assertEquals(longMessage, exception.message)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code, exception.errorCode)
    }

    @Test
    @DisplayName("Should handle special characters in message correctly")
    fun shouldHandleSpecialCharactersInMessageCorrectly() {
        // Given
        val messageWithSpecialChars = "Mail send failed: !@#$%^&*()_+-=[]{}|;':\",./<>?"

        // When
        val exception = MailSendException(message = messageWithSpecialChars)

        // Then
        assertNotNull(exception)
        assertEquals(messageWithSpecialChars, exception.message)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code, exception.errorCode)
    }

    @Test
    @DisplayName("Should handle nested cause exceptions correctly")
    fun shouldHandleNestedCauseExceptionsCorrectly() {
        // Given
        val rootCause = IllegalArgumentException("Invalid email address")
        val intermediateCause = RuntimeException("Mail validation failed", rootCause)

        // When
        val exception = MailSendException(cause = intermediateCause)

        // Then
        assertNotNull(exception)
        assertEquals(intermediateCause, exception.cause)
        assertEquals(rootCause, exception.cause?.cause)
    }

    @Test
    @DisplayName("Should handle multiple exception instances independently")
    fun shouldHandleMultipleExceptionInstancesIndependently() {
        // Given
        val message1 = "First mail send failure"
        val errorCode1 = "FIRST_MAIL_ERROR"
        val message2 = "Second mail send failure"
        val errorCode2 = "SECOND_MAIL_ERROR"

        // When
        val exception1 = MailSendException(message = message1, errorCode = errorCode1)
        val exception2 = MailSendException(message = message2, errorCode = errorCode2)

        // Then
        assertEquals(message1, exception1.message)
        assertEquals(errorCode1, exception1.errorCode)
        assertEquals(message2, exception2.message)
        assertEquals(errorCode2, exception2.errorCode)
    }

    @Test
    @DisplayName("Should use default technical error code enum values correctly")
    fun shouldUseDefaultTechnicalErrorCodeEnumValuesCorrectly() {
        // Given & When
        val exception = MailSendException()

        // Then
        assertNotNull(exception)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.defaultMessage, exception.message)
        assertEquals(TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code, exception.errorCode)
    }

    @Test
    @DisplayName("Should handle mail service specific error scenarios")
    fun shouldHandleMailServiceSpecificErrorScenarios() {
        // Given
        val smtpMessage = "SMTP authentication failed"
        val smtpErrorCode = "SMTP_AUTH_FAILED"
        val smtpCause = SecurityException("Invalid SMTP credentials")

        // When
        val smtpException = MailSendException(
            message = smtpMessage,
            errorCode = smtpErrorCode,
            cause = smtpCause
        )

        // Then
        assertNotNull(smtpException)
        assertEquals(smtpMessage, smtpException.message)
        assertEquals(smtpErrorCode, smtpException.errorCode)
        assertEquals(smtpCause, smtpException.cause)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, smtpException.httpStatusEnum)
    }
}
