package com.adsearch.domain.exception

import com.adsearch.domain.exception.enums.FunctionalErrorCodeEnum
import com.adsearch.domain.exception.enums.HttpStatusEnum

/**
 * Exception thrown when authentication fails
 */
class InvalidCredentialsException(
    message: String = FunctionalErrorCodeEnum.INVALID_CREDENTIALS.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.INVALID_CREDENTIALS.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, cause, errorCode, httpStatusEnum)
