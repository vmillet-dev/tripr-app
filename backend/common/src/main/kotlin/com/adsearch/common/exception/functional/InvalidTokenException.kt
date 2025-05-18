package com.adsearch.common.exception.functional

import com.adsearch.common.enum.FunctionalErrorCodeEnum
import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.exception.BaseFunctionalException

/**
 * Exception thrown when a token is invalid
 */
class InvalidTokenException(
    message: String = FunctionalErrorCodeEnum.INVALID_TOKEN.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.INVALID_TOKEN.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatusEnum, cause)
