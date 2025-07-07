package com.adsearch.infrastructure.adapter.`in`.web.handler

import com.adsearch.domain.exception.BaseFunctionalException
import com.adsearch.domain.model.enum.HttpStatusEnum
import com.adsearch.infrastructure.exception.BaseTechnicalException
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    private lateinit var globalExceptionHandler: GlobalExceptionHandler
    private val request = mockk<HttpServletRequest>()

    private class TestTechnicalException(
        message: String,
        cause: Throwable? = null,
        errorCode: String,
        httpStatusEnum: HttpStatusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR
    ) : BaseTechnicalException(message, cause, errorCode, httpStatusEnum)

    private class TestFunctionalException(
        message: String,
        cause: Throwable? = null,
        errorCode: String,
        httpStatusEnum: HttpStatusEnum = HttpStatusEnum.BAD_REQUEST
    ) : BaseFunctionalException(message, cause, errorCode, httpStatusEnum)

    @BeforeEach
    fun setUp() {
        globalExceptionHandler = GlobalExceptionHandler()
        every { request.requestURI } returns "/api/test"
    }

    @Test
    @DisplayName("Should handle BaseTechnicalException with correct error response")
    fun shouldHandleBaseTechnicalExceptionWithCorrectErrorResponse() {
        // Given
        val exception = TestTechnicalException("Technical error occurred", null, "TECH_ERROR", HttpStatusEnum.INTERNAL_SERVER_ERROR)

        // When
        val response = globalExceptionHandler.handleBBaseTechnicalException(exception, request)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body)
        assertEquals(500, response.body!!.status)
        assertEquals("TECH_ERROR", response.body!!.error)
        assertEquals("Technical error occurred", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should handle BaseTechnicalException with BAD_REQUEST status")
    fun shouldHandleBaseTechnicalExceptionWithBadRequestStatus() {
        // Given
        val exception = TestTechnicalException("Bad request error", null, "BAD_REQ_ERROR", HttpStatusEnum.BAD_REQUEST)

        // When
        val response = globalExceptionHandler.handleBBaseTechnicalException(exception, request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        assertEquals(400, response.body!!.status)
        assertEquals("BAD_REQ_ERROR", response.body!!.error)
        assertEquals("Bad request error", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should handle BaseFunctionalException with correct error response")
    fun shouldHandleBaseFunctionalExceptionWithCorrectErrorResponse() {
        // Given
        val exception = TestFunctionalException("Functional error occurred", null, "FUNC_ERROR", HttpStatusEnum.BAD_REQUEST)

        // When
        val response = globalExceptionHandler.handleBBaseFunctionalException(exception, request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        assertEquals(400, response.body!!.status)
        assertEquals("FUNC_ERROR", response.body!!.error)
        assertEquals("Functional error occurred", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should handle BaseFunctionalException with UNAUTHORIZED status")
    fun shouldHandleBaseFunctionalExceptionWithUnauthorizedStatus() {
        // Given
        val exception = TestFunctionalException("Unauthorized access", null, "UNAUTH_ERROR", HttpStatusEnum.UNAUTHORIZED)

        // When
        val response = globalExceptionHandler.handleBBaseFunctionalException(exception, request)

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(401, response.body!!.status)
        assertEquals("UNAUTH_ERROR", response.body!!.error)
        assertEquals("Unauthorized access", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with validation errors")
    fun shouldHandleMethodArgumentNotValidExceptionWithValidationErrors() {
        // Given
        val bindingResult = mockk<BindingResult>()
        val fieldError1 = FieldError("user", "username", "Username is required")
        val fieldError2 = FieldError("user", "email", "Email format is invalid")
        val exception = MethodArgumentNotValidException(mockk(), bindingResult)

        every { bindingResult.fieldErrors } returns listOf(fieldError1, fieldError2)

        // When
        val response = globalExceptionHandler.handleValidationExceptions(exception, request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        assertEquals(400, response.body!!.status)
        assertEquals("Bad Request", response.body!!.error)
        assertEquals("Validation failed: username: Username is required, email: Email format is invalid", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with single validation error")
    fun shouldHandleMethodArgumentNotValidExceptionWithSingleValidationError() {
        // Given
        val bindingResult = mockk<BindingResult>()
        val fieldError = FieldError("user", "password", "Password must be at least 8 characters")
        val exception = MethodArgumentNotValidException(mockk(), bindingResult)

        every { bindingResult.fieldErrors } returns listOf(fieldError)

        // When
        val response = globalExceptionHandler.handleValidationExceptions(exception, request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        assertEquals(400, response.body!!.status)
        assertEquals("Bad Request", response.body!!.error)
        assertEquals("Validation failed: password: Password must be at least 8 characters", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should handle AuthorizationDeniedException with forbidden status")
    fun shouldHandleAuthorizationDeniedExceptionWithForbiddenStatus() {
        // Given
        val exception = AuthorizationDeniedException("Access denied")

        // When
        val response = globalExceptionHandler.handleAuthorizationDeniedException(exception, request)

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNotNull(response.body)
        assertEquals(403, response.body!!.status)
        assertEquals("Forbidden", response.body!!.error)
        assertEquals("An unexpected error occurred: Access denied", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should handle generic Exception with internal server error status")
    fun shouldHandleGenericExceptionWithInternalServerErrorStatus() {
        // Given
        val exception = RuntimeException("Unexpected runtime error")

        // When
        val response = globalExceptionHandler.handleGenericException(exception, request)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body)
        assertEquals(500, response.body!!.status)
        assertEquals("Internal Server Error", response.body!!.error)
        assertEquals("An unexpected error occurred: Unexpected runtime error", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should handle NullPointerException as generic exception")
    fun shouldHandleNullPointerExceptionAsGenericException() {
        // Given
        val exception = NullPointerException("Null pointer encountered")

        // When
        val response = globalExceptionHandler.handleGenericException(exception, request)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body)
        assertEquals(500, response.body!!.status)
        assertEquals("Internal Server Error", response.body!!.error)
        assertEquals("An unexpected error occurred: Null pointer encountered", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should create error response with correct path from request URI")
    fun shouldCreateErrorResponseWithCorrectPathFromRequestUri() {
        // Given
        val customPath = "/api/custom/endpoint"
        every { request.requestURI } returns customPath
        val exception = RuntimeException("Path test error")

        // When
        val response = globalExceptionHandler.handleGenericException(exception, request)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body)
        assertEquals(customPath, response.body!!.path)
    }

    @Test
    @DisplayName("Should handle empty validation errors correctly")
    fun shouldHandleEmptyValidationErrorsCorrectly() {
        // Given
        val bindingResult = mockk<BindingResult>()
        val exception = MethodArgumentNotValidException(mockk(), bindingResult)

        every { bindingResult.fieldErrors } returns emptyList()

        // When
        val response = globalExceptionHandler.handleValidationExceptions(exception, request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        assertEquals(400, response.body!!.status)
        assertEquals("Bad Request", response.body!!.error)
        assertEquals("Validation failed: ", response.body!!.message)
        assertEquals("/api/test", response.body!!.path)
    }

    @Test
    @DisplayName("Should convert HttpStatusEnum to Spring HttpStatus correctly")
    fun shouldConvertHttpStatusEnumToSpringHttpStatusCorrectly() {
        // Given
        val badRequestException = TestTechnicalException("Bad request", null, "BAD_REQ", HttpStatusEnum.BAD_REQUEST)
        val unauthorizedException = TestTechnicalException("Unauthorized", null, "UNAUTH", HttpStatusEnum.UNAUTHORIZED)
        val internalErrorException = TestTechnicalException("Internal error", null, "INTERNAL", HttpStatusEnum.INTERNAL_SERVER_ERROR)

        // When
        val badRequestResponse = globalExceptionHandler.handleBBaseTechnicalException(badRequestException, request)
        val unauthorizedResponse = globalExceptionHandler.handleBBaseTechnicalException(unauthorizedException, request)
        val internalErrorResponse = globalExceptionHandler.handleBBaseTechnicalException(internalErrorException, request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.statusCode)
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedResponse.statusCode)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, internalErrorResponse.statusCode)
    }
}
