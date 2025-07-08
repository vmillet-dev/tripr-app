package com.adsearch.infrastructure.exception

import com.adsearch.domain.model.enum.HttpStatusEnum
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("Base Technical Exception Tests")
class BaseTechnicalExceptionTest {

    private class TestTechnicalException(
        message: String,
        cause: Throwable? = null,
        errorCode: String,
        httpStatusEnum: HttpStatusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR
    ) : BaseTechnicalException(message, cause, errorCode, httpStatusEnum)

    @Test
    @DisplayName("Should create exception with message and error code when provided")
    fun shouldCreateExceptionWithMessageAndErrorCodeWhenProvided() {
        // Given
        val message = "Test exception message"
        val errorCode = "TEST_ERROR_001"

        // When
        val exception = TestTechnicalException(message = message, errorCode = errorCode)

        // Then
        assertNotNull(exception)
        assertEquals(message, exception.message)
        assertEquals(errorCode, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
        assertNull(exception.cause)
    }

    @Test
    @DisplayName("Should create exception with custom HTTP status when provided")
    fun shouldCreateExceptionWithCustomHttpStatusWhenProvided() {
        // Given
        val message = "Bad request exception"
        val errorCode = "BAD_REQUEST_001"
        val httpStatus = HttpStatusEnum.BAD_REQUEST

        // When
        val exception = TestTechnicalException(
            message = message,
            errorCode = errorCode,
            httpStatusEnum = httpStatus
        )

        // Then
        assertNotNull(exception)
        assertEquals(message, exception.message)
        assertEquals(errorCode, exception.errorCode)
        assertEquals(httpStatus, exception.httpStatusEnum)
        assertNull(exception.cause)
    }

    @Test
    @DisplayName("Should create exception with cause when provided")
    fun shouldCreateExceptionWithCauseWhenProvided() {
        // Given
        val message = "Exception with cause"
        val errorCode = "CAUSED_ERROR_001"
        val cause = RuntimeException("Root cause exception")

        // When
        val exception = TestTechnicalException(
            message = message,
            cause = cause,
            errorCode = errorCode
        )

        // Then
        assertNotNull(exception)
        assertEquals(message, exception.message)
        assertEquals(errorCode, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
        assertEquals(cause, exception.cause)
    }

    @Test
    @DisplayName("Should create exception with all parameters when provided")
    fun shouldCreateExceptionWithAllParametersWhenProvided() {
        // Given
        val message = "Complete exception"
        val errorCode = "COMPLETE_ERROR_001"
        val httpStatus = HttpStatusEnum.BAD_REQUEST
        val cause = IllegalArgumentException("Invalid argument")

        // When
        val exception = TestTechnicalException(
            message = message,
            cause = cause,
            errorCode = errorCode,
            httpStatusEnum = httpStatus
        )

        // Then
        assertNotNull(exception)
        assertEquals(message, exception.message)
        assertEquals(errorCode, exception.errorCode)
        assertEquals(httpStatus, exception.httpStatusEnum)
        assertEquals(cause, exception.cause)
    }

    @Test
    @DisplayName("Should handle empty message correctly")
    fun shouldHandleEmptyMessageCorrectly() {
        // Given
        val emptyMessage = ""
        val errorCode = "EMPTY_MESSAGE_001"

        // When
        val exception = TestTechnicalException(message = emptyMessage, errorCode = errorCode)

        // Then
        assertNotNull(exception)
        assertEquals(emptyMessage, exception.message)
        assertEquals(errorCode, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
    }

    @Test
    @DisplayName("Should handle empty error code correctly")
    fun shouldHandleEmptyErrorCodeCorrectly() {
        // Given
        val message = "Exception with empty error code"
        val emptyErrorCode = ""

        // When
        val exception = TestTechnicalException(message = message, errorCode = emptyErrorCode)

        // Then
        assertNotNull(exception)
        assertEquals(message, exception.message)
        assertEquals(emptyErrorCode, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
    }

    @Test
    @DisplayName("Should handle different HTTP status enums correctly")
    fun shouldHandleDifferentHttpStatusEnumsCorrectly() {
        // Given
        val message = "Unauthorized exception"
        val errorCode = "UNAUTHORIZED_001"
        val httpStatus = HttpStatusEnum.UNAUTHORIZED

        // When
        val exception = TestTechnicalException(
            message = message,
            errorCode = errorCode,
            httpStatusEnum = httpStatus
        )

        // Then
        assertNotNull(exception)
        assertEquals(message, exception.message)
        assertEquals(errorCode, exception.errorCode)
        assertEquals(HttpStatusEnum.UNAUTHORIZED, exception.httpStatusEnum)
    }

    @Test
    @DisplayName("Should handle long messages correctly")
    fun shouldHandleLongMessagesCorrectly() {
        // Given
        val longMessage = "This is a very long exception message that contains many words and characters to test how the exception handles lengthy text content properly"
        val errorCode = "LONG_MESSAGE_001"

        // When
        val exception = TestTechnicalException(message = longMessage, errorCode = errorCode)

        // Then
        assertNotNull(exception)
        assertEquals(longMessage, exception.message)
        assertEquals(errorCode, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
    }

    @Test
    @DisplayName("Should handle special characters in message correctly")
    fun shouldHandleSpecialCharactersInMessageCorrectly() {
        // Given
        val messageWithSpecialChars = "Exception with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?"
        val errorCode = "SPECIAL_CHARS_001"

        // When
        val exception = TestTechnicalException(message = messageWithSpecialChars, errorCode = errorCode)

        // Then
        assertNotNull(exception)
        assertEquals(messageWithSpecialChars, exception.message)
        assertEquals(errorCode, exception.errorCode)
        assertEquals(HttpStatusEnum.INTERNAL_SERVER_ERROR, exception.httpStatusEnum)
    }

    @Test
    @DisplayName("Should handle nested cause exceptions correctly")
    fun shouldHandleNestedCauseExceptionsCorrectly() {
        // Given
        val message = "Exception with nested cause"
        val errorCode = "NESTED_CAUSE_001"
        val rootCause = IllegalStateException("Root cause")
        val intermediateCause = RuntimeException("Intermediate cause", rootCause)

        // When
        val exception = TestTechnicalException(
            message = message,
            cause = intermediateCause,
            errorCode = errorCode
        )

        // Then
        assertNotNull(exception)
        assertEquals(message, exception.message)
        assertEquals(errorCode, exception.errorCode)
        assertEquals(intermediateCause, exception.cause)
        assertEquals(rootCause, exception.cause?.cause)
    }

    @Test
    @DisplayName("Should maintain exception inheritance correctly")
    fun shouldMaintainExceptionInheritanceCorrectly() {
        // Given
        val message = "Inheritance test exception"
        val errorCode = "INHERITANCE_001"

        // When
        val exception = TestTechnicalException(message = message, errorCode = errorCode)

        // Then
        assertNotNull(exception)
        assertEquals(message, exception.message)
        assertEquals(errorCode, exception.errorCode)
    }

    @Test
    @DisplayName("Should handle multiple exception instances independently")
    fun shouldHandleMultipleExceptionInstancesIndependently() {
        // Given
        val message1 = "First exception"
        val errorCode1 = "FIRST_001"
        val httpStatus1 = HttpStatusEnum.BAD_REQUEST

        val message2 = "Second exception"
        val errorCode2 = "SECOND_002"
        val httpStatus2 = HttpStatusEnum.UNAUTHORIZED

        // When
        val exception1 = TestTechnicalException(
            message = message1,
            errorCode = errorCode1,
            httpStatusEnum = httpStatus1
        )
        val exception2 = TestTechnicalException(
            message = message2,
            errorCode = errorCode2,
            httpStatusEnum = httpStatus2
        )

        // Then
        assertEquals(message1, exception1.message)
        assertEquals(errorCode1, exception1.errorCode)
        assertEquals(httpStatus1, exception1.httpStatusEnum)

        assertEquals(message2, exception2.message)
        assertEquals(errorCode2, exception2.errorCode)
        assertEquals(httpStatus2, exception2.httpStatusEnum)
    }
}
