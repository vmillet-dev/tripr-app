package com.adsearch.domain.exception

import com.adsearch.domain.enum.FunctionalErrorCodeEnum
import com.adsearch.domain.enum.HttpStatusEnum

/**
 * Exception thrown when a token is expired
 */
class TokenExpiredException(
    message: String = FunctionalErrorCodeEnum.TOKEN_EXPIRED.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.TOKEN_EXPIRED.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, cause, errorCode, httpStatusEnum)
