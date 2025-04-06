package com.adsearch.common.exception

import com.adsearch.common.enum.FunctionalErrorCodeEnum
import org.springframework.http.HttpStatus

/**
 * Exception thrown when authentication fails
 */
class InvalidCredentialsException(
    message: String = FunctionalErrorCodeEnum.INVALID_CREDENTIALS.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.INVALID_CREDENTIALS.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)

/**
 * Exception thrown when a token is invalid
 */
class InvalidTokenException(
    message: String = FunctionalErrorCodeEnum.INVALID_TOKEN.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.INVALID_TOKEN.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)

/**
 * Exception thrown when a token is expired
 */
class TokenExpiredException(
    message: String = FunctionalErrorCodeEnum.TOKEN_EXPIRED.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.TOKEN_EXPIRED.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)

class UserAlreadyExistsException(
    message: String = FunctionalErrorCodeEnum.USER_ALREADY_EXISTS.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.USER_ALREADY_EXISTS.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)

class UserNotFoundException(
    message: String = FunctionalErrorCodeEnum.USER_NOT_FOUND.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.USER_NOT_FOUND.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)
