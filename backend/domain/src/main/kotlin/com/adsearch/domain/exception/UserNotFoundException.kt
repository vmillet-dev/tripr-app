package com.adsearch.domain.exception

import com.adsearch.domain.model.enum.FunctionalErrorCodeEnum
import com.adsearch.domain.model.enum.HttpStatusEnum

class UserNotFoundException(
    message: String = FunctionalErrorCodeEnum.USER_NOT_FOUND.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.USER_NOT_FOUND.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, cause, errorCode, httpStatusEnum)
