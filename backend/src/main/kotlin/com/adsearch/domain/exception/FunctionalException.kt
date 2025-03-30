package com.adsearch.domain.exception

import com.adsearch.domain.enum.ErrorCodeEnum
import org.springframework.http.HttpStatus

/**
 * Exception thrown when authentication fails
 */
class InvalidCredentialsException(
    message: String = ErrorCodeEnum.INVALID_CREDENTIALS.defaultMessage,
    errorCode: String = ErrorCodeEnum.INVALID_CREDENTIALS.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)

/**
 * Exception thrown when a token is invalid
 */
class InvalidTokenException(
    message: String = ErrorCodeEnum.INVALID_TOKEN.defaultMessage,
    errorCode: String = ErrorCodeEnum.INVALID_TOKEN.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)

/**
 * Exception thrown when a token is expired
 */
class TokenExpiredException(
    message: String = ErrorCodeEnum.TOKEN_EXPIRED.defaultMessage,
    errorCode: String = ErrorCodeEnum.TOKEN_EXPIRED.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)

class UserAlreadyExistsException(
    message: String = ErrorCodeEnum.USER_ALREADY_EXISTS.defaultMessage,
    errorCode: String = ErrorCodeEnum.USER_ALREADY_EXISTS.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)

class UserNotFoundException(
    message: String = ErrorCodeEnum.USER_NOT_FOUND.defaultMessage,
    errorCode: String = ErrorCodeEnum.USER_NOT_FOUND.code,
    httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatus, cause)
