package com.adsearch.domain.exception

/**
 * Exception thrown when a token has expired
 */
class TokenExpiredException(message: String) : DomainException(message)
