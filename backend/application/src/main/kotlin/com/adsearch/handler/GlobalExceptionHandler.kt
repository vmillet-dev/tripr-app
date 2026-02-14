package com.adsearch.handler

import com.adsearch.domain.exception.BaseFunctionalException
import com.adsearch.domain.model.enums.HttpStatusEnum
import com.adsearch.infrastructure.adapter.`in`.rest.dto.ErrorResponseDto
import com.adsearch.infrastructure.exception.BaseTechnicalException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BaseTechnicalException::class)
    fun handleBBaseTechnicalException(
        ex: BaseTechnicalException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        log.error("Exception occurred: ${ex.message}", ex)

        return createErrorResponse(
            status = ex.httpStatusEnum.toSpringHttpStatus(),
            error = ex.errorCode,
            message = ex.message,
            path = request.requestURI
        )
    }

    @ExceptionHandler(BaseFunctionalException::class)
    fun handleBBaseFunctionalException(
        ex: BaseFunctionalException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        log.warn("Exception occurred: ${ex.message}", ex)

        return createErrorResponse(
            status = ex.httpStatusEnum.toSpringHttpStatus(),
            error = ex.errorCode,
            message = ex.message,
            path = request.requestURI
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponseDto> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return createErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Validation failed: $errors",
            path = request.requestURI
        )
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(
        ex: AuthorizationDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        val errorResponse = createErrorResponse(
            status = HttpStatus.FORBIDDEN,
            error = HttpStatus.FORBIDDEN.reasonPhrase,
            message = "An unexpected error occurred: ${ex.message}",
            path = request.requestURI
        )

        log.error("Error occurred: ${errorResponse.body?.message}", ex)
        return errorResponse
    }


    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponseDto> {
        val errorResponse = createErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An unexpected error occurred: ${ex.message}",
            path = request.requestURI
        )

        log.error("Error occurred: ${errorResponse.body?.message}", ex)
        return errorResponse
    }

    private fun createErrorResponse(status: HttpStatus, error: String, message: String, path: String): ResponseEntity<ErrorResponseDto> {
        val errorResponseDto = ErrorResponseDto(
            status = status.value(),
            error = error,
            message = message,
            path = path
        )
        return ResponseEntity(errorResponseDto, status)
    }

    // Extension function to make the conversion more elegant
    private fun HttpStatusEnum.toSpringHttpStatus(): HttpStatus = when (this) {
        HttpStatusEnum.BAD_REQUEST -> HttpStatus.BAD_REQUEST
        HttpStatusEnum.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
        HttpStatusEnum.INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
    }
}

