package com.adsearch.domain.exception

/**
 * Base exception class for domain-specific exceptions
 */
abstract class DomainException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Exception thrown when an ad source is unavailable
 */
class SourceUnavailableException(
    val sourceName: String,
    cause: Throwable? = null
) : DomainException("Ad source '$sourceName' is currently unavailable", cause)

/**
 * Exception thrown when search criteria are invalid
 */
class InvalidSearchCriteriaException(
    message: String
) : DomainException(message)

/**
 * Exception thrown when an ad source returns an error
 */
class SourceErrorException(
    val sourceName: String,
    message: String,
    cause: Throwable? = null
) : DomainException("Error from ad source '$sourceName': $message", cause)
