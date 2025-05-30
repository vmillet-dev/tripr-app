package com.adsearch.common.exception.functional

import com.adsearch.common.enum.FunctionalErrorCodeEnum
import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.exception.BaseFunctionalException

/**
 * Exception thrown when authentication fails
 */
class InvalidCredentialsException(
    message: String = FunctionalErrorCodeEnum.INVALID_CREDENTIALS.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.INVALID_CREDENTIALS.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatusEnum, cause)
