package com.adsearch.domain.exception

/**
 * Exception thrown when authentication fails
 */
class AuthenticationException(message: String) : DomainException(message)

/**
 * Exception thrown when a token is invalid
 */
class InvalidTokenException(message: String) : DomainException(message)

/**
 * Exception thrown when a user is not found
 */
class UserNotFoundException(message: String) : DomainException(message)
