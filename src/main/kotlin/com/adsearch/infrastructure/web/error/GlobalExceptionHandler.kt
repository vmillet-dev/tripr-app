package com.adsearch.infrastructure.web.error

import com.adsearch.domain.exception.DomainException
import com.adsearch.domain.exception.InvalidSearchCriteriaException
import com.adsearch.domain.exception.SourceErrorException
import com.adsearch.domain.exception.SourceUnavailableException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.allErrors.associate { error ->
            val fieldName = (error as? FieldError)?.field ?: error.objectName
            fieldName to (error.defaultMessage ?: "Validation error")
        }
        
        logger.warn("Validation error: $errors")
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Validation Error",
                message = "Invalid request parameters",
                details = errors
            ))
    }
    
    /**
     * Handle invalid search criteria exception
     */
    @ExceptionHandler(InvalidSearchCriteriaException::class)
    fun handleInvalidSearchCriteria(
        ex: InvalidSearchCriteriaException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid search criteria: ${ex.message}")
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Invalid Search Criteria",
                message = ex.message ?: "Invalid search criteria provided",
                details = null
            ))
    }
    
    /**
     * Handle source unavailable exception
     */
    @ExceptionHandler(SourceUnavailableException::class)
    fun handleSourceUnavailable(
        ex: SourceUnavailableException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Source unavailable: ${ex.sourceName}")
        
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ErrorResponse(
                status = HttpStatus.SERVICE_UNAVAILABLE.value(),
                error = "Source Unavailable",
                message = ex.message ?: "Ad source is currently unavailable",
                details = mapOf("sourceName" to ex.sourceName)
            ))
    }
    
    /**
     * Handle source error exception
     */
    @ExceptionHandler(SourceErrorException::class)
    fun handleSourceError(
        ex: SourceErrorException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Source error: ${ex.sourceName} - ${ex.message}", ex)
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = "Source Error",
                message = ex.message ?: "Error occurred while fetching ads from source",
                details = mapOf("sourceName" to ex.sourceName)
            ))
    }
    
    /**
     * Handle other domain exceptions
     */
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(
        ex: DomainException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Domain exception: ${ex.message}", ex)
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = "Domain Error",
                message = ex.message ?: "An error occurred in the domain layer",
                details = null
            ))
    }
    
    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled exception: ${ex.message}", ex)
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = "Internal Server Error",
                message = "An unexpected error occurred",
                details = null
            ))
    }
}

/**
 * Standard error response format
 */
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val details: Map<String, String>?
)
