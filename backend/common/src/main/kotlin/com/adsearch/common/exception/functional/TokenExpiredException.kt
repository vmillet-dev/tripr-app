package com.adsearch.common.exception.functional

import com.adsearch.common.enum.FunctionalErrorCodeEnum
import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.exception.BaseFunctionalException

/**
 * Exception thrown when a token is expired
 */
class TokenExpiredException(
    message: String = FunctionalErrorCodeEnum.TOKEN_EXPIRED.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.TOKEN_EXPIRED.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatusEnum, cause)
