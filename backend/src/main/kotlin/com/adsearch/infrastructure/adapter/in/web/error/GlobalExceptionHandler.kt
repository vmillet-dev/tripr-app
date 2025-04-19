package com.adsearch.infrastructure.adapter.`in`.web.error

import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.enum.LogLevelEnum
import com.adsearch.common.exception.BaseException
import com.adsearch.infrastructure.adapter.`in`.web.dto.ErrorResponseDto
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException, request: HttpServletRequest): ResponseEntity<ErrorResponseDto> {
        logBaseException(ex)

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

    private fun logBaseException(exception: BaseException) {
        val logMessage = "Exception occurred: ${exception.message}"
        when (exception.logLevelEnum) {
            LogLevelEnum.ERROR -> log.error(logMessage, exception)
            LogLevelEnum.WARN -> log.warn(logMessage, exception)
        }
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

