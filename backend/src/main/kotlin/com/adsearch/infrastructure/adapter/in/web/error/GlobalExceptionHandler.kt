package com.adsearch.infrastructure.adapter.`in`.web.error

import com.adsearch.common.exception.BaseException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    companion object {
        val LOG: org.slf4j.Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logBaseException(ex)

        val errorResponse = ErrorResponse(
            status = ex.httpStatus.value(),
            error = ex.errorCode,
            message = ex.message,
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, ex.httpStatus)
    }

    // Spring's built-in exceptions
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Validation failed: $errors",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An unexpected error occurred: ${ex.message}",
            path = request.requestURI
        )
        LOG.error("Error occurred: ${errorResponse.message}", ex)

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun logBaseException(exception: BaseException) {
        when (exception.logLevel) {
            LogLevel.ERROR -> LOG.error("Exception occurred: ${exception.message}", exception)
            LogLevel.WARN -> LOG.warn("Exception occurred: ${exception.message}", exception)
            else -> LOG.info("Exception occurred: ${exception.message}", exception)
        }
    }

}

/**
 * Standard error response format
 */
data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
