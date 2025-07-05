package com.adsearch.domain.exception

import com.adsearch.domain.model.enum.FunctionalErrorCodeEnum
import com.adsearch.domain.model.enum.HttpStatusEnum

/**
 * Exception thrown when a token is invalid
 */
class InvalidTokenException(
    message: String = FunctionalErrorCodeEnum.INVALID_TOKEN.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.INVALID_TOKEN.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, cause, errorCode, httpStatusEnum)
